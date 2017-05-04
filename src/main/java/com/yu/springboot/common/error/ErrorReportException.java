package com.yu.springboot.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常信息类
 *
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-19
 */
public class ErrorReportException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ErrorReportException.class);

	private ErrorCtx errorCtx = new ErrorCtx();

	public ErrorReportException() {
	}

	public ErrorReportException(Exception ex) {
		if(ex instanceof ErrorReportException) {
			throw (ErrorReportException)ex;
		}
		log.warn("Not instanceof ErrorReportException: {}", ex);
		this.setErrorCtx(new ErrorCtx(ex));
	}

	public ErrorReportException(String errorCode, String errorMsg, Exception ex) {
		this.setErrorCtx(new ErrorCtx(errorCode, errorMsg, ex));
	}

	public ErrorReportException(String errorCode, Exception ex) {
		this(errorCode, "", ex);
	}

	public ErrorReportException(String errorCode, String errorMsg) {
		this(errorCode, errorMsg, null);
	}

	public ErrorReportException(ErrorCtx errorInfo) {
		this.setErrorCtx(errorInfo);
	}

	public ErrorCtx getErrorCtx() {
		return errorCtx;
	}

	public void setErrorCtx(ErrorCtx errorCtx) {
		this.errorCtx = errorCtx;
	}
}
