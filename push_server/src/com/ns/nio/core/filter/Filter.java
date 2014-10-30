package com.ns.nio.core.filter;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.session.IoSession;

/**
 * 
 * 过滤器，当事件触发时，要执行的过滤器（可定义多个进行外部控制）
 * 
 */
public interface Filter{

	/**
	 * 过滤器初始化方法
	 * @throws NioException
	 */
	public void init() throws NioException;
	
	/**
	 * 客户端与服务器创建会话的事件，即Socket连接成功事件
	 * @param session
	 * @throws Exception
	 */
	public void onConnect(IoSession session)throws NioException;
	
	/**
	 *  客户端与服务器关闭会话的事件，即Socket关闭事件 
	 * @param session
	 * @throws Exception
	 */
	public void onCloseSession(IoSession session)throws NioException;
	
	/**
	 * 收到数据事件
	 * @throws Exception
	 */
	public void onRead(IoSession session)throws NioException;
	
	/**
	 * 过滤器初始化方法
	 * @throws NioException
	 */
	public void onWrite(IoSession session) throws NioException;
	
	public Object onSendMsg(Object msg) throws NioException;
	
	/**
	 * 过滤器初始化方法
	 * @throws NioException
	 */
	public void onRegister(IoSession session)throws NioException;
	
	/**
	 * 过滤器销毁方法
	 * @throws Exception
	 */
	public void onDestroy() throws NioException;
	
	/**
	 * 返回过滤器的排序序号
	 * @return
	 */
	public int getOrder();
	
}