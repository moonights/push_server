package com.ns.nio.core.event;
/**
 *事件枚举
 */
public enum EventEnum {
	E_CREATE_SESSION,   	//新建Session事件
	E_REGISTER,				//注册Client中的Session到服务端事件
	E_CLOSE_SESSION,    	//关闭Session事件
	E_READ_DATA,  			//接受数据事件
	E_WRITE_DATA,         	//发送数据事件
	E_QUIT,             	//退出程序事件
}