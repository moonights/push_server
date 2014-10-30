package com.ns.nio.simple;

import com.ns.nio.core.NioSocketConnector;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.session.IoSession;
import com.ns.nio.utils.ConfigUtils;

public class Client extends Thread{ 
	private IoSession session;
	private NioSocketConnector connector;		
	private boolean isRunning=true;		
	private String msg;		
	
    public Client() {
    	connector = new NioSocketConnector();
		connector.addFilter(new ClientFilter(2));
		connector.bindIoHandler(new SimpleIoHandler());
		try {
			connector.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.connect();
    }
    
    
    /**
     * 注册用户
     * 权限判断
     * 
     * */
    public void register(String u,String p){
    	session.register(u, p);
    }
    
    public void connect(){
    	try {
    		String host=ConfigUtils.getStr("host");
    		int port=ConfigUtils.getInt("port");
			session =connector.connect(host,port);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("客户端启动成功.\nsessionid:"+session.getId());
		} catch (NioException e) {
			e.printStackTrace();
		}
    } 
    
    
    /**发送信息给服务端**/
    public void sendMessageToServer(String msg){
    	session.wirte(msg);
    }
    
    /**此方法需要放到线程中**/
    public String getMessageFromServer(){
    	Object obj=session.getReceiveMessage();
    	if(null!=obj){
    		msg=obj.toString();
    	}
    	//接收到信息后 置空
    	session.setReceiveMessage(null);
    	return msg;
    }
	
	public void close(){
		isRunning=false;
	}
    
	public void run(){
		try {
			while(isRunning){
				String msg=this.getMessageFromServer();
				if(null!=msg){
					System.out.println("Client接收到信息:"+msg);
				}
				Thread.sleep(500);
			}
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			session.close();
		}
	}
}
