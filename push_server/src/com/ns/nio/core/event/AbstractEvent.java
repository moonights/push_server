package com.ns.nio.core.event;

import com.ns.nio.core.session.IoSession;

/**定义抽象的事件类**/
public abstract class AbstractEvent {
	
	public EventEnum eventEnum;
	
	public Object data;
	
	public AbstractEvent(){
		
	}
	
	/**
	 * @param eventEnum
	 * @param data
	 */
	public AbstractEvent(EventEnum eventEnum, Object data) {
		super();
		this.eventEnum = eventEnum;
		this.data = data;
	}

	public EventEnum getEventEnum() {
		return eventEnum;
	}
	public void setEventEnum(EventEnum eventEnum) {
		this.eventEnum = eventEnum;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}