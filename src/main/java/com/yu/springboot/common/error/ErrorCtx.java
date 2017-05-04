package com.yu.springboot.common.error;

import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 异常消息对象
 *
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-19
 */
public class ErrorCtx {

	private final static String _DEFAULT_ERROR_CODE = "-9999";
	private final static String _DEFAULT_ERROR_MESSAGE = "no errMsg.";
	private final static String _DEFAULT_EX_DETAIL = "";

	private String errCode;
	private String errMsg;
	private Object exDetail;

	public ErrorCtx() {
	}

	public static ErrorCtx newInstance(String errCode, String errMsg) {
		ErrorCtx rtnCtx = new ErrorCtx();
		rtnCtx.setErrCode(errCode);
		rtnCtx.setErrMsg(errMsg);
		rtnCtx.setExDetail(_DEFAULT_EX_DETAIL);
		return rtnCtx;
	}

	public ErrorCtx(String errCode, String errMsg, Exception ex) {
		this.errCode = StringUtils.hasText(errCode) ? errCode :  _DEFAULT_ERROR_CODE;
		this.errMsg = StringUtils.hasText(errMsg) ? errMsg : _DEFAULT_ERROR_MESSAGE;
		if(ex != null){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ex.printStackTrace(new PrintStream(out));
			this.exDetail = new String(out.toByteArray());
		}else{
			this.exDetail = _DEFAULT_EX_DETAIL;
		}
	}

	public ErrorCtx(String errCode, String errMsg) {
		this(errCode, errMsg, null);
	}

	public ErrorCtx(String errMsg) {
		this(null,errMsg);
	}

	public ErrorCtx(Exception ex) {
		this(null, ex.getClass().getName(), ex);
	}

	public ErrorCtx(String errMsg, Exception ex) {
		this(null,errMsg,ex);
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getExDetail() {
		return exDetail;
	}

	public void setExDetail(Object exDetail) {
		this.exDetail = exDetail;
	}
}
