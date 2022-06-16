package com.spring.boot.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class XSSFilter implements Filter {


  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(
        ServletRequest request, 
        ServletResponse response, 
        FilterChain chain) throws IOException, ServletException {
      XSSRequestWrapper wrappedRequest = new XSSRequestWrapper((HttpServletRequest) request);
      chain.doFilter(wrappedRequest, response);
   }

} 