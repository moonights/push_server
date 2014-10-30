package com.ns.nio.core.event;

import com.ns.nio.core.session.IoSession;

/**事件分发**/
public class EventDispatcher{
	
	
	/**
	 * 业务线程事件分发
	 * 
	 * @param event
	 * 
	 */
//	public void doEvent(Event event) {
//		if (event != null) {
//			switch (event.getEventEnum()) {
//			case ON_CREATE_SESSION:
//				logger.info("执行创建session事件.");
//				doCreateSession(event.getSc());
//				break;
//			case ON_READ_DATA:
//				logger.info("执行doRead事件.");
//				Session session = event.getSession();
//				System.out.println(session.getReceiveMessage());
//				doRead(session);
//				break;
//			case ON_WRITE_DATA:
//				logger.info("执行doWrite事件.");
//				doWrite(event);
//				break;
//			case ON_CLOSE_SESSION:
//				logger.info("执行ON_CLOSE_SESSION事件.");
//				onCloseSession(event.getSc());
//				break;
//			case ON_QUIT:
//				break;
//			}
//		}
//	}
	
}
