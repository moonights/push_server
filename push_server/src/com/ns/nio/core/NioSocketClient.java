/**
 * 
 */
package com.ns.nio.core;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author moonights
 * NIO客户端
 */
public class NioSocketClient extends NioSocketProcessor{

	private static final long serialVersionUID = -985120455013895458L;

	public NioSocketClient() {
		super(NioSocketEnum.CLIENT);
	}

	/* (non-Javadoc)
	 * @see com.ns.nio.core.NioSocketProcessor#getKey(java.nio.channels.SocketChannel)
	 */
	@Override
	protected String getKey(SocketChannel sc)throws Exception {
		InetSocketAddress remote=(InetSocketAddress)sc.socket().getRemoteSocketAddress();
		String key=remote.getAddress().getHostAddress()+":"+remote.getPort();
		return key;
	}
	
	
	
	
	
}
