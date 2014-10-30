package com.ns.nio.core.session;

import java.util.Date;


/***
 * 
 * @author moonights
 * 客户端与服务器建立的连接会话，保存一些必要的信息
 */
public interface IoSession {
	
	
	public boolean isRegister();
	/**
	 * 获取会话ID
	 * @return
	 */
	public long getId();
	
	/**
	 * 获取会话创建时间
	 * @return
	 */
	public Date getCreateDate();
	

	/**
	 * 注册到服务器
	 * */
	public void register(String u,String p);
	
	
	/**
	 * 写数据
	 * @param buffer
	 */
	public void wirte(Object buffer);
	
	/**
	 * 获取收到的数据
	 * @return
	 */
	public Object getReceiveMessage();
	
	/**
	 * 设置接收到数据，用于收到报文以后转化成业务对象
	 * @param msg
	 */
	public void setReceiveMessage(Object msg);
	
	/**
	 * 设置属性，以便以后的过滤器需要
	 * @param key
	 * @param value
	 */
	public void setAttribute(Object key, Object value);
	
	/**
	 * 获取设置的属性
	 * @param key
	 * @return
	 */
	public Object getAttribute(Object key);
	
	/**
	 * 获取本地IP地址
	 * @return
	 */
	public String  getLocalIp();
	
	/**
	 * 获取本地端口号
	 * @return
	 */
	public int  getLocalPort();
	
	/**
	 * 获取远程IP地址
	 * @return
	 */
	public String  getRemoteIp();
	
	/**
	 * 获取远程端口号
	 * @return
	 */
	public int  getRemotePort();
	
	/**
	 * 关闭会话
	 */
	public void close();
	
}