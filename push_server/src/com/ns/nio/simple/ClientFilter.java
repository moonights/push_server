package com.ns.nio.simple;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.session.IoSession;

/**
 * 当客户端连接上以后马上给客户端发送一个报文
 */
public class ClientFilter implements Filter {
	
	private static Logger logger  =  Logger.getLogger(ClientFilter.class );
	
	private int order;
	
	public ClientFilter(int order){
		this.order=order;
	}
	

	public int getOrder() {
		return order;
	}

	public void onCloseSession(IoSession session) throws NioException {
		session.close();
		logger.debug(this.toString()+"onCloseSession");
	}

	public void onConnect(IoSession session) throws NioException {
		logger.debug(this.toString()+"onConnect");
	}
	
	public String toString(){
		return order+"-inputFilter:";
	}
	
	/* (non-Javadoc)
	 * @see com.ns.nio.core.filter.InputFilter#onRead(com.ns.nio.core.session.Session)
	 */
	@Override
	public void onRead(IoSession session) throws NioException {
		Object data=session.getReceiveMessage();
		if(null!=data){
			String s=data.toString();
			logger.debug(session.getLocalIp()+":"+session.getLocalPort()+"接收服务端数据:"+s);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.ns.nio.core.filter.InputFilter#onWrite(com.ns.nio.core.session.Session)
	 */
	@Override
	public void onWrite(IoSession session) throws NioException {
		logger.debug(this.toString()+"onWrite。。。。");
	} 
	
	
	public void onRegister(IoSession session)throws NioException{
		logger.debug(this.toString()+"onRegister。。。。");
	}
	
	@Override
	public Object onSendMsg(Object msg) {
		int n=msg.toString().getBytes().length;
		int len=(""+n).length();
		StringBuffer sb= new StringBuffer(1024);
		for(;len<8;len++){
			sb.append("0");
		}
		sb.append(n);
		sb.append(msg.toString());
		byte [] src= sb.toString().getBytes();
		ByteBuffer bb=ByteBuffer.allocate(src.length);
		bb.put(src,0,src.length);
		return bb;
	}
	public void onDestroy() throws NioException {
		logger.debug(this.toString()+"destroy");
	}
	public void init() throws NioException {
		logger.debug(this.toString()+"init");
	}
}