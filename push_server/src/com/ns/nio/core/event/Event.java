package com.ns.nio.core.event;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

import com.ns.nio.core.session.IoSession;

/**自定义事件**/
public class Event extends AbstractEvent  implements Serializable{
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = -4988449284472712313L;
	private IoSession session;
	private SocketChannel sc;
	
	public Event(EventEnum eventEnum) {
		super(eventEnum,null);
	}
	
	public Event(EventEnum eventEnum, Object data) {
		super(eventEnum,data);
	}
	
	public SocketChannel getSc() {
		return sc;
	}

	public void setSc(SocketChannel sc) {
		this.sc = sc;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}
}