/**
 * 
 */
package com.ns.nio.simple;

import java.io.IOException;

import com.ns.nio.core.NioSocketAcceptor;
import com.ns.nio.core.exception.NioException;
import com.ns.nio.utils.ConfigUtils;

/**
 * @author moonights
 *
 */
public class Server{	
	
	private static String HOST;
	private static int PORT;	
	private NioSocketAcceptor acceptor;	
	private static Server instance;
    static {
    	HOST=ConfigUtils.getStr("host");
    	PORT=ConfigUtils.getInt("port");
    }
	
    public static Server getInstance() {
		if (instance == null) {
            synchronized (Server.class) {
            	try {
        			instance = (Server) Class.forName("com.ns.nio.simple.Server").newInstance();
        		} catch (Throwable t) {
        			//throw new NsException("Cannot instantiate SessionManager ....", t);
        		}
            }
        }
        return instance;
	}
	
    public void init() throws NioException, IOException{
    	acceptor=new NioSocketAcceptor();
		acceptor.addFilter(new ServerFilter(1));
		acceptor.bindIoHandler(new SimpleIoHandler());
		acceptor.bind(HOST, PORT);
    }
    
    public void start(){
    	try {
			acceptor.init();
		} catch (NioException e) {
			e.printStackTrace();
		}
    }
    
    public void close(){
    	try {
			acceptor.close();
		} catch (NioException e) {
			e.printStackTrace();
		}
    }
    public void push(String msg){
    	acceptor.push(msg);
    }
    
}
