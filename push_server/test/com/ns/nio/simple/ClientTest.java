package com.ns.nio.simple;

import com.ns.nio.simple.Client;

/**
 * 客户端测试类
 */

public class ClientTest {

	private static int MAX_CLIENTS=1;
	
	public static void main(String[] args) {
		
		Client ct= null;		
		for(int i=0;i<MAX_CLIENTS;i++){
			ct=new Client();
			ct.register("moonights"+i,"000000");
			
			ct.start();
			ct.sendMessageToServer("Hello Server");
			try {
				Thread.sleep(400);
//				ct.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}