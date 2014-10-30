/**
 * 
 */
package com.ns.nio.core.session;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ns.nio.core.NioSocketProcessor;
import com.ns.nio.core.event.Event;
import com.ns.nio.core.event.EventEnum;
import com.ns.nio.core.handler.IoHandler;

/**
 * @author moonights
 *
 */
public class NioSession implements IoSession,Serializable{
	private static final long serialVersionUID = 275007776523644378L;
	private final static Logger logger = Logger.getLogger(NioSession.class);	
	private boolean isRegister;
	private String username;
	private String password;
	private SocketChannel channel;
	private long id;
	private String localIp;
	private int localPort;
	private String remoteIp;
	private int remotePort;
	private long lastActiveTime;
	private Date createDate;
	private Object attachment;
	private Object receivedMessage;
	private NioSocketProcessor processor;
	private NioSessionClient nsc;
	private IoHandler handler;
	
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
	
	
	public NioSession(String username,String password,String localIp,int localPort, String remoteIp,int remotePort,SocketChannel channel,NioSocketProcessor processor){
		this.username=username;
		this.password=password;
		this.localIp=localIp;
		this.localPort=localPort;
		this.remoteIp= remoteIp;
		this.remotePort=remotePort;
		createDate= new Date();
		this.processor=processor;
		id= UUID.randomUUID().getLeastSignificantBits();
		this.channel=channel;
	}
	
	public NioSession(SocketChannel channel,NioSocketProcessor processor){
		this.channel=channel;		
	}
	
	public NioSession(String localIp,int localPort, String remoteIp,int remotePort,NioSocketProcessor processor){
		this.localIp=localIp;
		this.localPort=localPort;
		this.remoteIp= remoteIp;
		this.remotePort=remotePort;
		createDate= new Date();
		this.processor=processor;
		id= UUID.randomUUID().getLeastSignificantBits();
	}
	
	public void close() {
		try {
			processor.closeSession(this);	
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the localIp
	 */
	public String getLocalIp() {
		return localIp;
	}

	/**
	 * @param localIp the localIp to set
	 */
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	/**
	 * @return the localPort
	 */
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * @param localPort the localPort to set
	 */
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/**
	 * @return the remoteIp
	 */
	public String getRemoteIp() {
		return remoteIp;
	}

	/**
	 * @param remoteIp the remoteIp to set
	 */
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	/**
	 * @return the remotePort
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * @param remotePort the remotePort to set
	 */
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * @return the lastActiveTime
	 */
	public long getLastActiveTime() {
		return lastActiveTime;
	}

	/**
	 * @param lastActiveTime the lastActiveTime to set
	 */
	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the attachment
	 */
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the receivedMessage
	 */
	public Object getReceivedMessage() {
		return receivedMessage;
	}

	/**
	 * @param receivedMessage the receivedMessage to set
	 */
	public void setReceivedMessage(Object receivedMessage) {
		this.receivedMessage = receivedMessage;
	}

	
	
	/**
	 * @return the processor
	 */
	public NioSocketProcessor getProcessor() {
		return processor;
	}

	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(NioSocketProcessor processor) {
		this.processor = processor;
	}

	/**
	 * @return the channel
	 */
	public SocketChannel getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return the handler
	 */
	public IoHandler getHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#wirte(java.lang.Object)
	 */
	@Override
	public void wirte(Object buffer) {
		Event event= new Event(EventEnum.E_WRITE_DATA,buffer);
		event.setSession(this);
		processor.addEventQueue(event);
	}
	
	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#register(java.lang.String,java.lang.String)
	 */
	@Override
	public void register(String u,String p) {
		this.setUsername(u);
		this.setPassword(p);
		this.nsc=new NioSessionClient(u,p,this.localIp,this.localPort,this.channel);
		Event event= new Event(EventEnum.E_REGISTER,this.nsc);
		event.setSession(this);
		processor.addEventQueue(event);
	}

	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#getReceiveMessage()
	 */
	
	@Override
	public Object getReceiveMessage() {
		return receivedMessage;
	}
	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#setReceiveMessage(java.lang.Object)
	 */
	@Override
	public void setReceiveMessage(Object msg) {
		receivedMessage=msg;
	}
	
	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#setAttribute(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttribute(Object key, Object value) {
		
	}

	/* (non-Javadoc)
	 * @see com.ns.nio.core.session.Session#getAttribute(java.lang.Object)
	 */
	@Override
	public Object getAttribute(Object key) {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public boolean isRegister() {
		return this.isRegister;
	}
	
	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}
}
