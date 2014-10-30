/**
 * 
 */
package com.ns.nio.core;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.ns.nio.core.NioSocketProcessor.NioSocketEnum;
import com.ns.nio.core.event.Event;
import com.ns.nio.core.event.EventEnum;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.handler.IoHandler;
import com.ns.nio.core.protocol.Protocol;
import com.ns.nio.core.session.IoSession;
import com.ns.nio.core.session.NioSession;

/**
 * @author moonights
 *socket 连接器 供客户端使用
 */
public final class NioSocketConnector {
	private NioSocketProcessor processor;
	private IoSession session;
	public NioSocketConnector(){
		processor=new NioSocketClient();
	}
	
	public void init() throws Exception{
		processor.startup();
	}
	
	public IoSession connect(SocketAddress address) throws NioException{
		try {
			SocketChannel sc = SocketChannel.open(address);
			//创建一个Session会话 绑定一个Socket
			session=processor.doCreateSession(sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	/***注册客户端***/
	public void regester(String u,String p){
		NioSession session_=(NioSession)session;
		session_.setUsername(u);
		session_.setPassword(p);
		session.wirte(session_);
//		session.wirte(Protocol.P_HEAD+"="+Protocol.P_JOIN+Protocol.P_C+
//				Protocol.P_CLIENT_U+"="+u+Protocol.P_C+
//				Protocol.P_CLIENT_P+"="+p+Protocol.P_C);
	}
	
	/**
	 * 添加过滤器
	 * @param filter
	 */
	public void addFilter(Filter filter){
		processor.addFilter(filter);
	}
	
	/**
	 * 绑定ioHandler
	 * @param ioHandler
	 */
	public void bindIoHandler(IoHandler ioHandler){
		processor.bindIoHandler(ioHandler);
	}
	
	public IoSession connect(String hostName,int port) throws NioException{
		SocketAddress address = new InetSocketAddress(hostName,port);
		return this.connect(address);
	}
	
	public void  onRecevie(){
		Event event=new Event(EventEnum.E_READ_DATA);
		processor.addEventQueue(event);
	}
}
