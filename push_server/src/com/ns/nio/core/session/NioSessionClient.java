package com.ns.nio.core.session;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

/*****
 * 专门序列化的子类
 * @ClassName: NioSessionClient
 * @Description: TODO
 * @author Administrator
 * @date 2014-8-15 下午1:40:24
 */
public class NioSessionClient implements Serializable{
	private static final long serialVersionUID = 3938914853411981653L;
	private String username;
	private String password;
	private String localIp;
	private int localPort;
	private SocketChannel channel;
	
	
	public NioSessionClient(String username, String password, String localIp,
			int localPort, SocketChannel channel) {
		super();
		this.username = username;
		this.password = password;
		this.localIp = localIp;
		this.localPort = localPort;
		this.channel = channel;
	}
	
//	private void writeObject(ObjectOutputStream out)throws IOException{  
//        out.writeObject(channel);  
//    }  
	
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public SocketChannel getChannel() {
		return channel;
	}
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}
}
