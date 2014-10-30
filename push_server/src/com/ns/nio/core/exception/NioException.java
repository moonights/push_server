package com.ns.nio.core.exception;

/***
 * NIO异常
 * @author moonights
 *
 */
public class  NioException extends Exception {
	
	private static final long serialVersionUID = -126868509367975678L;
	public static String ERROR_CODE_1="0001"; 
	public static String ERROR_CODE_2="0002"; 
	public static String ERROR_CODE_3="0003"; 
	public static String ERROR_CODE_4="0004"; 

	private String errorCode;
	public NioException() {
		super();
		// TODO Auto-generated constructor stub
	}
	public NioException(String exception){
		super(exception);
	}
	public NioException(String errorCode,String exception){
		super(exception);
		this.errorCode=errorCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	
	public NioException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NioException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
