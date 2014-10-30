package com.ns.nio.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ns.nio.core.event.Event;
import com.ns.nio.core.event.EventEnum;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.handler.IoHandler;
import com.ns.nio.core.session.IoSession;
import com.ns.nio.core.session.NioSession;


/***
 * @author moonights
 * 服务器端的接收器
 * 供服务端调用
 */
public final class NioSocketAcceptor {
	private final static Logger logger = Logger.getLogger(NioSocketAcceptor.class);
	
    /* 作为服务器监听的端口与SocketServer的映射关系*/	
	private NioSocketProcessor processor;	
	
	private ConcurrentHashMap<Integer,ServerSocketChannel> servers = new ConcurrentHashMap<Integer,ServerSocketChannel>(); 
	
	private ReentrantLock  lock = new ReentrantLock(true);
	
	public NioSocketAcceptor() throws NioException{
		processor=new NioSocketServer();
	}
	
    public void init()throws NioException {
        processor.startup();
	}
    
    public void close()throws NioException {
    	processor.close();
	}
    
	public void bind(String ip,int port)throws NioException, IOException{
		ServerSocketChannel  serverSocket  = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(ip,port);
		//ssc.socket().setReuseAddress(true);		//是否复用地址
		serverSocket.socket().bind(address);		//绑定端口
		serverSocket.configureBlocking(false);		//设置为异步模式
		lock.lock();
		try {
			this.servers.put(port, serverSocket);
			//注册socket事件
			serverSocket.register(processor.getSelector(), SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
    /**
	 * 添加过滤器
	 * @param filter
	 */
	public void addFilter(Filter filter){
		processor.addFilter(filter);
	}
	
	public void bindIoHandler(IoHandler ioHandler){
		processor.bindIoHandler(ioHandler);
	}
    
	/***给所有客户端推送消息****/
	public void push(String msg){
		Event event=new Event(EventEnum.E_WRITE_DATA);
		Map<SocketChannel,IoSession> sessions=processor.getSessions();
		if(null!=sessions){
			Iterator<SocketChannel> iter = sessions.keySet().iterator();
			while(iter.hasNext()){
				SocketChannel sc=(SocketChannel)iter.next();
				logger.info("socket:"+sc.socket().getInetAddress().getHostAddress()+":"+sc.socket().getPort());		
				//给所有人发
				IoSession session=(IoSession)sessions.get(sc);		
				event.setSession(session);
				event.setData(msg);				
				processor.doEvent(event);
				//给一个人发
//				NioSession session=(NioSession)sessions.get(sc);
//				logger.info("Username:"+session.getUsername());
//				if(null!=session.getUsername()&&session.getUsername().equals("moonights0")){
//					event.setSession(session);
//					event.setData(msg);				
//					processor.doEvent(event);
//				}
			}
		}
	}
	
}
