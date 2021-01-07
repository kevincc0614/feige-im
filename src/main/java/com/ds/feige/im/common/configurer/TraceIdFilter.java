package com.ds.feige.im.common.configurer;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import org.springframework.stereotype.Component;

import com.ds.feige.im.common.util.Tracer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
@Component
public class TraceIdFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        Tracer.getTraceId(request);
        chain.doFilter(request, response);
        Tracer.removeTraceId();

    }
}
