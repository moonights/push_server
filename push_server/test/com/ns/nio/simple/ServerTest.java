/**
 * 
 */
package com.ns.nio.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.simple.Server;

/**
 * @author moonights
 *
 */
public class ServerTest {
	
	public static void main(String[] args) throws NioException, IOException {
		final Server server=new Server();
		server.init();
		server.start();
		
		//测试是否正常关闭资源
//		try {
//			Thread.sleep(2000);
//			server.close();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						InputStreamReader input = new InputStreamReader(System.in);
						BufferedReader br = new BufferedReader(input);
						String sendText = br.readLine();
						server.push(sendText);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
