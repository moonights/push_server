package com.ns.nio.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RecevierTest {

	public static void main(String[] args) {
		try {
			SocketAddress address = new  InetSocketAddress("127.0.0.1",8090);
			SocketChannel client= SocketChannel.open(address);
			
			client.configureBlocking(false);
			ByteBuffer buf=ByteBuffer.allocate(1024);
			while(true){				
				buf.clear();
				while(true){
					int i=client.read(buf);
					if(i>0){
						buf.flip();
						byte[] b=new byte[buf.limit()];
						buf.get(b,buf.position(),buf.limit());
						System.out.println("服务端传来数据:"+new String(b,"utf-8"));
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
