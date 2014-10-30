package com.ns.nio.simple;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ns.nio.core.exception.NioException;
import com.ns.nio.core.filter.Filter;
import com.ns.nio.core.session.IoSession;

/**
 * 
 * 当客户端连接上以后马上给客户端发送一个报文
 * 
 */
public class ServerFilter implements Filter {
	private static Logger logger  =  Logger.getLogger(ServerFilter.class );
	private int order;
	
	public ServerFilter(int order){
		this.order=order;
	}
	public int getOrder() {
		return order;
	}

	public void init() throws NioException {
		logger.debug(this.toString()+"init");
	}

	public void onCloseSession(IoSession session) throws NioException {
		session.close();
		logger.debug(this.toString()+"onCloseSession");
	}

	public void onConnect(IoSession session) throws NioException {
		logger.debug(this.toString()+"onCreateSession");
		String s="tip=heatbeat;user_type=admin;sessionid="+session.getId();
		session.wirte(s);
	}

	
	/* (non-Javadoc)
	 * @see com.ns.nio.core.filter.InputFilter#onRead(com.ns.nio.core.session.Session)
	 */
	@Override
	public void onRead(IoSession session) throws NioException {
		String s=null;
		if(null!=session.getReceiveMessage()){
			s=session.getReceiveMessage().toString();
		}
		logger.debug(session.getRemoteIp()+":"+session.getRemotePort()+"接收客户端数据:"+s);
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

	public String toString(){
		return order+"-inputFilter:";
	}
}