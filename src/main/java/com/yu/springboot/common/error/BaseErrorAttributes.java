package com.yu.springboot.common.error;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常属性
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 * @see org.springframework.boot.autoconfigure.web.DefaultErrorAttributes
 *
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-19
 */
@Configuration
public class BaseErrorAttributes implements ErrorAttributes, HandlerExceptionResolver{
    private static final String ERROR_ATTRIBUTE = BaseErrorAttributes.class.getName() + ".ERROR";

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        this.storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_ATTRIBUTE, ex);
    }

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        LinkedHashMap errorAttributes = new LinkedHashMap();
        this.addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
        return errorAttributes;
    }

    private void addErrorDetails(Map<String, Object> errorAttributes, RequestAttributes requestAttributes, boolean includeStackTrace) {
        Throwable error = this.getError(requestAttributes);
        if(error != null) {
            if(!(error instanceof ServletException) || error.getCause() == null) {
                if(error instanceof BindingResult || error instanceof MethodArgumentNotValidException){
                    this.addBindingResult(errorAttributes, error);
                }else{
                    this.addErrorCtx(errorAttributes, error);
                }
                //
                if(includeStackTrace) {
                    this.addStackTrace(errorAttributes, error);
                }
            }
        }
        if(errorAttributes.size() == 0 ){
            this.addServletEx(errorAttributes, requestAttributes);
        }
    }

    /**
     * Thrown exception message 2 ErrorCtx
     * @param errorAttributes
     * @param error
     */
    private void addErrorCtx (Map<String, Object> errorAttributes, Throwable error){
        ErrorCtx errorCtx = null;
        if(error instanceof ErrorReportException){
            errorCtx = ((ErrorReportException)error).getErrorCtx();
        }else if(error instanceof Exception){
            errorCtx = new ErrorCtx(StringUtils.hasText(error.getMessage())?error.getMessage():(StringUtils.hasText(error.getClass().getName())? error.getClass().getName():"No errMsg available."),(Exception)error);
        }else{
            errorCtx = new ErrorCtx(StringUtils.hasText(error.getMessage())?error.getMessage():(StringUtils.hasText(error.getClass().getName())? error.getClass().getName():"No errMsg available."));
        }
        //
        errorAttributes.put("rtnCode", errorCtx.getErrCode());
        errorAttributes.put("rtnMsg", errorCtx.getErrMsg());
        errorAttributes.put("bean", errorCtx.getExDetail());
    }

    /**
     * Resolve BindingResult
     * @param errorAttributes
     * @param error
     */
    private void addBindingResult(Map<String, Object> errorAttributes, Throwable error) {
        errorAttributes.put("rtnCode", "-9997");
        BindingResult result = this.extractBindingResult(error);
        if(result == null) {
            errorAttributes.put("rtnMsg", error.getMessage());
        } else {
            if(result.getErrorCount() > 0) {
                String msg = "";
                for(ObjectError objectError : result.getAllErrors()){
                    msg += objectError.getDefaultMessage();
                }
                errorAttributes.put("rtnMsg", StringUtils.hasText(msg)?msg:"Validation failed for object=\'" + result.getObjectName() + "\'. Error count: " + result.getErrorCount());
                errorAttributes.put("exDetail", result.getAllErrors());
            } else {
                errorAttributes.put("rtnMsg", "No errors");
            }

        }
    }

    /**
     * Thrown servletException
     * @param errorAttributes
     * @param requestAttributes
     */
    private void addServletEx(Map<String, Object> errorAttributes,RequestAttributes requestAttributes){
        Integer status = (Integer)this.getAttribute(requestAttributes, "javax.servlet.error.status_code");
        String message = (String)this.getAttribute(requestAttributes, "javax.servlet.error.message");
        //
        errorAttributes.put("rtnCode",status == null ? "-9998" : String.valueOf(status));
        errorAttributes.put("rtnMsg",StringUtils.isEmpty(message)?"No message available":message);
        //
        if(status != null){
            try {
                errorAttributes.put("exDetail", HttpStatus.valueOf(status.intValue()).getReasonPhrase());
            } catch (Exception e) {
                errorAttributes.put("exDetail", "Http Status " + status);
            }
        }
    }

    /**
     * Add stack trace
     * @param errorAttributes
     * @param error
     */
    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private BindingResult extractBindingResult(Throwable error) {
        return error instanceof BindingResult?(BindingResult)error:(error instanceof MethodArgumentNotValidException ?((MethodArgumentNotValidException)error).getBindingResult():null);
    }

    @Override
    public Throwable getError(RequestAttributes requestAttributes) {
        Throwable exception = (Throwable)this.getAttribute(requestAttributes, ERROR_ATTRIBUTE);
        if(exception == null) {
            exception = (Throwable)this.getAttribute(requestAttributes, "org.springframework.web.servlet.DispatcherServlet.EXCEPTION");
            //exception = (Throwable)this.getAttribute(requestAttributes, "javax.servlet.error.exception");
        }
        return exception;
    }

    private Object getAttribute(RequestAttributes requestAttributes, String name) {
        return requestAttributes.getAttribute(name, 0);
    }
}
