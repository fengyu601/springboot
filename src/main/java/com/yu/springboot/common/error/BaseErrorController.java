package com.yu.springboot.common.error;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
/**
 * 统一异常处理
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 * @see org.springframework.boot.autoconfigure.web.AbstractErrorController
 * @see org.springframework.boot.autoconfigure.web.BasicErrorController
 *
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-14
 */
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
//@RequestMapping(value = "/error")
public class BaseErrorController implements ErrorController {
    private ErrorAttributes errorAttributes;
    private List<ErrorViewResolver> errorViewResolvers;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    public BaseErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers){
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
        this.errorViewResolvers = this.sortErrorViewResolvers(errorViewResolvers);
    }

    private List<ErrorViewResolver> sortErrorViewResolvers(List<ErrorViewResolver> resolvers) {
        ArrayList sorted = new ArrayList();
        if(resolvers != null) {
            sorted.addAll(resolvers);
            AnnotationAwareOrderComparator.sortIfNecessary(sorted);
        }
        return sorted;
    }

    @RequestMapping(
            produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return modelAndView == null?new ModelAndView("error/error", model):modelAndView;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = this.getStatus(request);
        return new ResponseEntity(body, status);
    }

    /**
     * 获取错误信息
     * @param request
     * @param includeStackTrace
     * @return
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request,boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }

    /**
     * 判断匹配顺序
     * 1.ErrorProperties.IncludeStacktrace.ALWAYS
     * 2.ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM ? request.getParameter("trace") : false
     * @param request
     * @param produces
     * @return
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
        return include == ErrorProperties.IncludeStacktrace.ALWAYS?true:(include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM?this.getTraceParameter(request):false);
    }

    /**
     * 请求参数是否包含trace
     * @param request
     * @return
     */
    protected boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter == null?false:!"false".equals(parameter.toLowerCase());
    }

    /**
     * 获取HTTP状态码
     * @param request
     * @return
     */
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode.intValue());
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

    /**
     * 解析匹配错误视图
     * @param request
     * @param response
     * @param status
     * @param model
     * @return
     */
    protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status, Map<String, Object> model) {
        Iterator var5 = this.errorViewResolvers.iterator();

        ModelAndView modelAndView;
        do {
            if(!var5.hasNext()) {
                return null;
            }

            ErrorViewResolver resolver = (ErrorViewResolver)var5.next();
            modelAndView = resolver.resolveErrorView(request, status, model);
        } while(modelAndView == null);

        return modelAndView;
    }

    /**
     * 实现错误路径
     * @return
     */
    @Override
    public String getErrorPath() {
        return this.serverProperties.getError().getPath();
    }

}
