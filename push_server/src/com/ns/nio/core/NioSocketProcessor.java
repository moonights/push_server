/**
 * 
 */
package com.ns.nio.core;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ns.nio.core.event.Event;
import com.ns.nio.core.event.EventEnum;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.filter.FilterComparator;
import com.ns.nio.core.handler.IoHandler;
import com.ns.nio.core.session.IoSession;
import com.ns.nio.core.session.NioSession;
import com.ns.nio.core.session.NioSessionClient;

/**
 * 
 * @author moonights socket处理器
 */
public abstract class NioSocketProcessor  implements Serializable  {
	
	private static final long serialVersionUID = 3398728711360874734L;

	private final static Logger logger = Logger.getLogger(NioSocketProcessor.class);

	/** 运行标志 */
	private boolean isRunning = true;
	private Selector selector;
	private IoHandler ioHandler;
	private Notifier notifier;
	
	private NioSocketEnum socketEnum;	//区分server client
	
	public enum NioSocketEnum {
		SERVER,CLIENT
	}
	
	/** Socket事件队列 */
	// private static BlockingQueue<Event> eventQueue = new  ArrayBlockingQueue<Event>(1000);
	private ConcurrentLinkedQueue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();

	/** Socket事件队列锁 */
	private ReentrantLock eventQueueLock = new ReentrantLock(true);

	/** Socket事件队列锁条件 */
	private Condition eventQueueCondition = eventQueueLock.newCondition();

	/** Socket与Session之间的映射关系 */
	private Map<SocketChannel, IoSession> socketChannelSessionMap = new ConcurrentHashMap<SocketChannel, IoSession>();

	/** 过滤器集合，按照order进行排序 */
	private List<Filter> filterList = new ArrayList<Filter>();

	/** 发送数据前需要执行的过滤器集合，按照order进行排序 */

	/**
	 * key的生成规则： 对于服务器使用,服务器本地IP:服务器本地监听端口 对于客户端使用，连接服务器IP:连接服务器端口
	 * @return
	 */
	protected abstract String getKey(SocketChannel sc) throws Exception;

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	public NioSocketProcessor(NioSocketEnum socketEnum) {
		this.socketEnum=socketEnum;
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startup() throws NioException {
		logger.debug("初始化读数据过滤器........");
		notifier.fireOnInit();
		//线程池启动
		ExecutorService executor = Executors.newFixedThreadPool(10);
		logger.debug("初始化连接处理线程........");
        executor.execute(new ConnectProcessor(selector));
        logger.debug("初始化读写处理线程........");
        executor.execute(new EventProcessor());
        executor.shutdown();
        logger.debug("启动成功........");
	}
	
	
	/**
	 * 关闭释放资源
	 */
	public void close(){
		isRunning=false;
		Event event= new Event(EventEnum.E_QUIT);
		addEventQueue(event);
		
		//关闭所有的Socket连接
		for(SocketChannel sc: socketChannelSessionMap.keySet()){
			try {
				if(sc.isOpen()){
					sc.close();
					event.setSc(sc);
					this.doCloseSession(event);
				}
			} catch (Exception e) {
				logger.error("关闭session出现错误.....");
			}
		}
		socketChannelSessionMap.clear();
		//释放所有IOHander的资源
		try {
			this.ioHandler.destroy();
		} catch (NioException e1) {
			logger.error("关闭ioHandler出现错误.....");
		}
		
		//释放过滤器资源
		notifier.fireOnDestroy();
	}

	public Map<SocketChannel, IoSession> getSessions() {
		if (socketChannelSessionMap.size() == 0) {
			return null;
		}
		return socketChannelSessionMap;
	}

	public void bindIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}

	/**
	 * 添加事件
	 * 
	 * @param filter
	 */
	public void addEventQueue(Event event) {
		eventQueueLock.lock();
		try {
			eventQueue.add(event);
			eventQueueCondition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventQueueLock.unlock();
		}
	}

	/**
	 * 添加过滤器
	 * 
	 * @param filter
	 */
	public void addFilter(Filter filter) {
		if (notifier == null) {
			notifier=Notifier.getNotifier();
		}
		if (filter instanceof Filter) {
			notifier.addFilter(filter);
		} 
	}

	/**
	 * 注册Socket事件
	 * 
	 * @param session
	 * @param event
	 * @throws IOException
	 */
	public void registerSocket(IoSession session, int event) throws IOException {
		Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();
		while (iter.hasNext()) {
			SocketChannel key = iter.next();
			if (key.isOpen()&& session.equals(socketChannelSessionMap.get(key))) {
				key.register(selector, event);
				selector.wakeup();
				break;
			}
		}
	}
	
	
	
	/**
	 * 分发线程事件
	 * 
	 * @param key
	 */
	public void doEvent(SelectionKey key) {		
		if (key.isValid() && key.isAcceptable()) {		// Socket建立连接事件
			ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
			SocketChannel sc = null;
			try {
				sc = socketChannel.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 创建session
			Event event = new Event(EventEnum.E_CREATE_SESSION);
			event.setSc(sc);
			addEventQueue(event);
			
			// 注册到服务端
//			event = new Event(EventEnum.E_REGISTER);
//			event.setSc(sc);
//			addEventQueue(event);
		} else if (key.isValid() && key.isReadable()) {// 读取数据事件
			SocketChannel sc = (SocketChannel) key.channel();
			if (sc.isOpen()) {
				onRead(sc);
			}
		} else if (key.isValid() && key.isWritable()) {// 写数据事件
			logger.info("onWrite");
			SocketChannel sc = (SocketChannel) key.channel();
			if (sc.isOpen()) {
				
			}
		} else if (!key.isValid()) {				// Socket 关闭事件
			SocketChannel sc = (SocketChannel) key.channel();
			Event event = new Event(EventEnum.E_CLOSE_SESSION);
			event.setSc(sc);
			addEventQueue(event);
		} 
	}

	/**
	 * 从Socket读取数据，构造接收数据事件放入事件队列，等待业务事件线程处理
	 * 
	 * @param sc
	 */
	private void onRead(SocketChannel sc) {
		try {
			if (this.ioHandler == null) {
				logger.error("未绑定ioHandler");
				return;
			}
			Object msg=null;
			IoHandler handler = this.ioHandler;
			IoSession session = socketChannelSessionMap.get(sc);
			if(this.socketEnum==NioSocketEnum.SERVER){
				msg=handler.onRead(sc);
				if(msg!=null&&msg instanceof NioSessionClient){
					System.out.println("服务端读事件1。。。");
					NioSessionClient nsc=((NioSessionClient)msg);
	            	System.out.println("---------接收序列化对象getUsername-----="+nsc.getUsername());
	            	System.out.println("---------接收序列化对象sc-----="+nsc.getChannel());
	            	System.out.println("---------接收序列化对象socket-----="+nsc.getLocalIp()+":"+nsc.getLocalPort());
	            	
	            	//设置服务端的客户信息....
	            	Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();	            	
	    			while(iter.hasNext()){
	    				SocketChannel key=iter.next();
	    				if(key.socket().getPort()==nsc.getLocalPort()){
	    					System.out.println("---------key.socket().getPort()socket-----="+key.socket().getPort());
	    					NioSession s = (NioSession)socketChannelSessionMap.get(key);
	    					s.setUsername(nsc.getUsername());
	    					s.setPassword(nsc.getPassword());
	    					socketChannelSessionMap.put(key, s);
	    				}
	    			}
	            }else{	      
					System.out.println("服务端读事件2。。。");
					this.registerSocket(session, SelectionKey.OP_READ);
					session.setReceiveMessage(msg);
	            }
			}
			if(this.socketEnum==NioSocketEnum.CLIENT){
				System.out.println("客户端读事件。。。");
				msg = handler.onRead(sc);		
				this.registerSocket(session, SelectionKey.OP_READ);
				session.setReceiveMessage(msg);		
			}
			Event event = new Event(EventEnum.E_READ_DATA);
			event.setSession(session);
			addEventQueue(event);			
		} catch (NioException e) {
//			e.printStackTrace();
			//此处异常有些古怪 ，关闭后正常,程序后不断进行onRead操作
			Event event = new Event(EventEnum.E_CLOSE_SESSION);
			event.setSc(sc);
			doCloseSession(event);
		} catch (Exception e) {
//			e.printStackTrace();
			Event event = new Event(EventEnum.E_CLOSE_SESSION);
			event.setSc(sc);
			doCloseSession(event);
		}
	}
	
	
	/**
	 * 关闭一个session
	 * @param session
	 * @throws CommException
	 */
	public void closeSession(IoSession session) throws NioException{
		try {
			Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();
			while(iter.hasNext()){
				SocketChannel key=iter.next();
				if(session.equals(socketChannelSessionMap.get(key))){
					SelectionKey skey =key.keyFor(selector);
					if(skey!=null){ 
						skey.cancel(); //取消事件注册
					}
					key.close();
					
					//1构造:Session关闭事件
					Event event= new Event(EventEnum.E_CLOSE_SESSION);
					//2事件所需:需要SocketChannel 以便移除客户端Map的键值
					event.setSc(key);
					//3加入事件队列等待处理
					addEventQueue(event);
					
					break;
				}
			}
		} catch (Exception e) {
			throw new NioException(e);
		}
	}
	
	
	/**
	 * 
	 * 业务线程事件分发
	 * @param event
	 * 
	 */
	public void doEvent(Event event) {
		if (event != null) {
			switch (event.getEventEnum()) {
				case E_CREATE_SESSION:
					doCreateSession(event);	
				case E_REGISTER:
					doRegister(event);	
					break;
				case E_READ_DATA:		
					doRead(event);			
					break;
				case E_WRITE_DATA:		
					doWrite(event);
					break;
				case E_CLOSE_SESSION:	
					doCloseSession(event);
					break;
				case E_QUIT:
					break;
			}
		}
	}


	/**
	 * 
	 * 1.创建Session会话事件处理
	 * @param sc
	 * @return
	 */
	protected IoSession doCreateSession(Event event) {
		return this.doCreateSession(event.getSc());
	}

	protected IoSession doCreateSession(SocketChannel sc) {
		IoSession session = null;
		try {
			// 配置非阻塞方式
			sc.configureBlocking(false);
			InetSocketAddress remote = (InetSocketAddress) sc.socket().getRemoteSocketAddress();			
			session = new NioSession(sc.socket().getLocalAddress()
					.getHostAddress(), sc.socket().getLocalPort(), remote
					.getAddress().getHostAddress(), remote.getPort(), this);
			// 调用过滤器onCreateSession
			notifier.fireOnConnect(session);
			//1.如果是服务端
			if(this.socketEnum==NioSocketEnum.SERVER){
				logger.debug("服务端创建session="+session.getId());
				// 建立Socket与Session的映射关系
				socketChannelSessionMap.put(sc, session);
			}
			//2.如果是客户端
			if(this.socketEnum==NioSocketEnum.CLIENT){
				logger.debug("客户端创建session="+session.getId());
				socketChannelSessionMap.put(sc, session);
			}
			this.registerSocket(session, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	/**注册用户（客户端登录服务器 将session write到server,server端读取session注册到socketChannelSessionMap中）**/
	public void doRegister(Event event) {
		SocketChannel sc = null;
		//客户端的注册事件-发送session到服务端
		if(this.socketEnum==NioSocketEnum.CLIENT){
			logger.info("客户端发送session开始..."+this.socketEnum);
			try {
				IoSession session = event.getSession();
				Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();
				while (iter.hasNext()) {
					SocketChannel key = iter.next();
					if (session.equals(socketChannelSessionMap.get(key))) {
						sc = key;
						break;
					}
				}
				if (sc != null) {
					IoHandler handler = this.ioHandler;
					if (sc.isOpen()) {
						handler.onWrite(sc, event.getData());
					}
					// 发送完成以后注册读数据事件
					registerSocket(session, SelectionKey.OP_READ);
				}
			} catch (NioException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("客户端发送session结束...");
		}	
		
	}
	/**
	 * 2.read事件处理
	 * 
	 * @param session
	 */
	private void doRead(Event event) {
		IoSession session = event.getSession();
		try {
			logger.debug("onRead:开始执行input过滤器");
			notifier.fireOnRead(session);
			logger.debug("onRead:结束执行input过滤器");
			
			//服务端的注册事件-将客户端的session更新到socketChannelSessionMap
//			IoHandler handler = this.ioHandler;
//			SocketChannel sc = null;
//			if(this.socketEnum==NioSocketEnum.SERVER){
//				Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();
//				while (iter.hasNext()) {
//					SocketChannel key = iter.next();
//					if (session.equals(socketChannelSessionMap.get(key))) {
//						sc = key;
//						break;
//					}
//				}
//				if (sc != null) {
//					if (sc.isOpen()) {
//						Object obj=handler.readSerializableObject(sc);
//						logger.info("readSerializableObject="+obj);
//					}
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 3.write事件处理
	 * 
	 * @param event
	 */
	private void doWrite(Event event) {
		SocketChannel sc = null;
		try {
			IoSession session = event.getSession();
			Object data = event.getData();
			logger.debug("开始执行output过滤器");
			notifier.fireOnSendMsg(data);
			logger.debug("结束执行output过滤器");
			Iterator<SocketChannel> iter = socketChannelSessionMap.keySet().iterator();
			while (iter.hasNext()) {
				SocketChannel key = iter.next();
				if (session.equals(socketChannelSessionMap.get(key))) {
					sc = key;
					break;
				}
			}
			if (sc != null) {
				IoHandler handler = this.ioHandler;
				if (sc.isOpen()) {
					handler.onWrite(sc, data);
				}
				// 发送完成以后注册读数据事件
				registerSocket(session, SelectionKey.OP_READ);
			}
		} catch (NioException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 4.关闭Socket
	 * @param sc
	 * 
	 */
	private void doCloseSession(Event event) {
		SocketChannel sc = event.getSc();
		try {
			IoSession session = socketChannelSessionMap.get(sc);
			// 移除Socket与Session的映射关系
			socketChannelSessionMap.remove(sc);
			sc.close();
			// 调用过滤器onCloseSession
			notifier.fireOnCloseSession(session);
		} catch (Exception e) {
			logger.error("关闭session出错...");
		}
	}
	
	
	/**
	 * Socket 连接处理接收线程
	 */
	class ConnectProcessor extends Thread{
		private Selector selector;
		public ConnectProcessor(Selector selector){
			this.selector=selector;
		}
		public void run() {
			try {
				boolean isWorking;
				while (isRunning) {
					isWorking = false;
					int selected = selector.select(2000);
					if (selected > 0) {
						Set<SelectionKey> keySet = selector.selectedKeys();
						if (!keySet.isEmpty()) {
							isWorking = true;
							for (SelectionKey key : keySet) {
								doEvent(key);//执行事件
							}
							keySet.clear();
						}
					}
					if (!isWorking) {
						Thread.sleep(5);
					}
				}
				logger.info("ConnectProcessor退出");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读写业务处理线程
	 */
	class EventProcessor extends Thread {
		public void run() {
			Event event = null;
			while (isRunning) {
				eventQueueLock.lock();
				try {
					// 等待队列中有数据插入
					while (eventQueue.isEmpty()) {
						eventQueueCondition.await();
					}
					// 判断是否运行，否就退出线程
					if (!isRunning) {
						eventQueueLock.unlock();
						break;
					}
					// 安全检查,判断队列是否空
					if (eventQueue.isEmpty()) {
						eventQueueLock.unlock();
						continue;
					}
					event = eventQueue.poll();
					eventQueueCondition.signalAll();// 唤醒其他的等待该条件的条件锁
					// 处理事件
				} catch (Exception e) {
					eventQueueLock.unlock();
					e.printStackTrace();
				} finally {
					if (eventQueueLock.isLocked())
						eventQueueLock.unlock();
				}
				doEvent(event);
			}
			logger.info("EventProcessor退出");
		}
	}
}
