package com.ns.nio.simple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.handler.AbstractIoHandler;

/**
 * 
 * 报文定义
 * 
 */
public class SimpleIoHandler extends AbstractIoHandler {
	private static Logger logger = Logger.getLogger(SimpleIoHandler.class);

	@Override
	public void onAccept() throws NioException {
		// TODO Auto-generated method stub
		logger.info("#onAccept");
	}

	@Override
	public void destroy() throws NioException {
		logger.info("#destroy");
	}

	protected int getLenth(byte[] lenth) {
		System.out.println(Integer.parseInt(new String(lenth)));
		return Integer.parseInt(new String(lenth));
	}

	protected int getLenthOfLenth() {
		return 8;
	}

	protected boolean hasContainHead() {
		return false;
	}
}