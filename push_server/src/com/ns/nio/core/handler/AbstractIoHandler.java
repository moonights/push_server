package com.ns.nio.core.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.session.NioSession;
import com.ns.nio.core.session.NioSessionClient;

/**
 * 读写数据的抽象类
 * 
 */
public abstract class AbstractIoHandler implements IoHandler {
	private static Logger logger = Logger.getLogger(AbstractIoHandler.class);

	private static int BUFFER_SIZE = 1024;
	/**
	 * 
	 * 接收序列化对象
	 * 
	 */
	/**
	 * 读数据
	 */
	public synchronized Object onRead(SocketChannel sc) throws NioException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Object body = null;
		try {
			// 报文头
			ByteBuffer head = ByteBuffer.allocate(getLenthOfLenth());
			int rec = -1;
			if (sc.isOpen()) {
				rec = sc.read(head);
				head.flip();
			}
			if (rec == -1) {
				throw new NioException("NIO读取异常:远程Socket关闭");
			} else if (rec == 0) {
				return null;
			} else if (rec != head.limit()) {
				throw new NioException("NIO读取异常:接收报文头不全："
						+ new String(head.array()));
			}

			ByteBuffer msg = null;
			if (hasContainHead()) {
				msg = ByteBuffer.allocate(getLenth(head.array())
						- getLenthOfLenth());
			} else {
				msg = ByteBuffer.allocate(getLenth(head.array()));
			}
			while (msg.position() != msg.limit()) {
				rec = sc.read(msg);
				if (rec == -1) {
					throw new NioException("NIO读取异常:远程Socket关闭");
				} else if (rec == 0) {
					continue;
				}
			}
			msg.position(0);
			stream.write(head.array());
			stream.write(msg.array());
			byte[] buffer = stream.toByteArray();
			body = buffer;
			head.clear();
			stream.close();
		} catch (NioException e) {
			throw e;
		} catch (Exception e) {
			throw new NioException("NIO 读取异常.", e);

		}
		return body;
	}
	
//	public synchronized Object onRead(SocketChannel sc) throws NioException {
//		logger.info("readSerializableObject序列化对象...");
//		Object body = null;
//		try {
//			// 报文头
//			ByteBuffer head = ByteBuffer.allocate(1024);
//			int rec = -1;
//			if (sc.isOpen()) {
//				try {
//					rec = sc.read(head);
//				} catch (Exception e) {
//					sc.close();
//				}
//				head.flip();
//			}
//			if (rec > 0) {
//				ByteArrayInputStream byte_is = new ByteArrayInputStream(
//						head.array());
//				ObjectInputStream object_is = new ObjectInputStream(byte_is);
//				body = object_is.readObject();
//				head.clear();
//				byte_is.close();
//				object_is.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return body;
//	}
//	/**
//	 * 读数据，
//	 */
//	public synchronized Object onRead(SocketChannel sc) throws NioException {
//		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
//		int off = 0;
//		int r = 0;
//		byte[] data = new byte[BUFFER_SIZE * 10];
//
//		while (true) {
//			buffer.clear();
//			try {
//				r = sc.read(buffer);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			if (r == -1)
//				break;
//			if ((off + r) > data.length) {
//				data = grow(data, BUFFER_SIZE * 10);
//			}
//			byte[] buf = buffer.array();
//			System.arraycopy(buf, 0, data, off, r);
//			off += r;
//		}
//		byte[] req = new byte[off];
//		System.arraycopy(data, 0, req, 0, off);
//		return req;
//	}

//
//	/**
//	 * 循环发送数据
//	 */
//	public void onWrite(SocketChannel sc, Object buffer) throws NioException {
//		try {
//			ByteBuffer bb = (ByteBuffer) buffer;
//			bb.position(0);
//			while (bb.position() < bb.limit()) {
//				sc.write(bb);
//			}
//			logger.info("发送数据：" + new String(bb.array()));
//		} catch (Exception e) {
//			throw new NioException("NIO发送异常", e);
//		}
//	}

	public static byte[] grow(byte[] src, int size) {
		byte[] tmp = new byte[src.length + size];
		System.arraycopy(src, 0, tmp, 0, src.length);
		return tmp;
	}
	/**
	 * 
	 * 发送序列化对象
	 * 
	 */
	public void onWrite(SocketChannel sc, Object object)
			throws NioException {
		try {
			ByteArrayOutputStream byte_os = new ByteArrayOutputStream();
			ObjectOutputStream object_os = new ObjectOutputStream(byte_os);
			object_os.writeObject(object);
			object_os.flush();
			byte[] arr = byte_os.toByteArray();
			System.out.println("Object in " + arr.length + " bytes");
			ByteBuffer buffer = ByteBuffer.wrap(arr);
			buffer.position(0);
			while (buffer.position() < buffer.limit()) {
				sc.write(buffer);
			}
			object_os.close();
			logger.info("发送数据：" + new String(buffer.array()));
		} catch (Exception e) {
			throw new NioException("NIO发送异常", e);
		}
	}

	

	/**
	 * 获取报文长度的长度
	 * 
	 * @return
	 */
	protected abstract int getLenthOfLenth();

	/**
	 * 获取报文长度
	 * 
	 * @param lenth
	 * @return
	 */
	protected abstract int getLenth(byte[] lenth);

	/**
	 * 报文头的本身长度是否包涵在报文头里面
	 * 
	 * @return
	 */
	protected abstract boolean hasContainHead();

}