/**
 * 
 */
package com.ns.nio.core;

import java.nio.channels.SocketChannel;

import com.ns.nio.core.NioSocketProcessor.NioSocketEnum;
/**
 * 
 * @author moonights
 * 服务端的处理器
 */
public class NioSocketServer extends NioSocketProcessor {

	public NioSocketServer() {
		super(NioSocketEnum.SERVER);
	}

	@Override
	protected String getKey(SocketChannel sc)throws Exception {
		String key=sc.socket().getLocalAddress().getHostAddress()+":"+sc.socket().getLocalPort();
		return key;
	}

}
