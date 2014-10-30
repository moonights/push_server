package com.ns.nio.core;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.filter.FilterComparator;
import com.ns.nio.core.session.IoSession;

/** 
 * 
 * 事件分发器
 * 单例
 * @author moonights 
 * 
 * */
public class Notifier {
	private final static Logger logger = Logger.getLogger(Notifier.class);
	private static ArrayList<Filter> filters = null;
	private static Notifier instance = null;

	private Notifier() {
		filters = new ArrayList<Filter>();
		// 过滤器排序
		FilterComparator filterComparator = new FilterComparator();
		Collections.sort(filters, filterComparator);
	}

	/**
	 * 获取事件触发器
	 * @return 返回事件触发器
	 */
	public static synchronized Notifier getNotifier() {
		if (instance == null) {
			instance = new Notifier();
			return instance;
		} else{
			return instance;
		}
	}
	
	 /**
     * 添加事件监听器
     * @param filter 监听器
     */
    public void addFilter(Filter filter) {
        synchronized (filters) {
            if (!filters.contains(filter))
            	filters.add(filter);
        }
    }
    
    public void fireOnInit(){
        for (int i = filters.size() - 1; i >= 0; i--){
			try {
				( (Filter) filters.get(i)).init();
			} catch (NioException e) {
				logger.error("filters init error.");
			}
        }
    }
    
    public void fireOnDestroy() {
    	  for (int i = filters.size() - 1; i >= 0; i--){
  			try {
  				( (Filter) filters.get(i)).onDestroy();
  			} catch (NioException e) {
  				logger.error("filters onDestroy error.");
  			}
          }
    }
    
	public void fireOnConnect(IoSession session) {
		for (int i = filters.size() - 1; i >= 0; i--) {
			try {
				((Filter) filters.get(i)).onConnect(session);
			} catch (NioException e) {
				logger.error("filters onConnect error.");
			}
		}
	}

    public void fireOnRead(IoSession session) {
  	  for (int i = filters.size() - 1; i >= 0; i--){
			try {
				( (Filter) filters.get(i)).onRead(session);
			} catch (NioException e) {
				logger.error("filters onRead error.");
			}
        }
    }
    
    public void fireOnCloseSession(IoSession session) {
    	for (int i = filters.size() - 1; i >= 0; i--){
    		try {
    			( (Filter) filters.get(i)).onCloseSession(session);
    		} catch (NioException e) {
    			logger.error("filters onCloseSession error.");
    		}
    	}
    }
    
    public void fireOnSendMsg(Object msg) {
    	for (int i = filters.size() - 1; i >= 0; i--){
    		try {
    			( (Filter) filters.get(i)).onSendMsg(msg);
    		} catch (NioException e) {
    			logger.error("filters onSendMsg error.");
    		}
    	}
    }
    
}
