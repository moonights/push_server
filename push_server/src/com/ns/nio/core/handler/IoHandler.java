package com.ns.nio.core.handler;

import java.nio.channels.SocketChannel;
import com.ns.nio.core.exception.NioException;

/**
 * Socket读写接口
 */
public interface IoHandler {

	/* 初始化Socket读写接口 */
	public void onAccept() throws NioException;

	/* Socket读方法 */
	public Object onRead(SocketChannel sc) throws NioException;

	/* Socket写方法 */
	public void onWrite(SocketChannel sc, Object buffer) throws NioException;

	/* Socket销毁 */
	public void destroy() throws NioException;

}