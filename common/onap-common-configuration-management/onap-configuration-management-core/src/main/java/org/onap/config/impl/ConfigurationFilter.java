package org.onap.config.impl;

import org.onap.config.Constants;
import org.onap.config.api.Configuration;

import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



@WebFilter("/")
public class ConfigurationFilter implements Filter {

  @Override
  public void init(FilterConfig paramFilterConfig) throws ServletException {
    //Use the default behavior
  }

  @Override
  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse,
                       FilterChain paramFilterChain) throws IOException, ServletException {
    Configuration.tenant.set(Constants.DEFAULT_TENANT);
    try {
      paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
    } finally {
      Configuration.tenant.remove();
    }
  }

  @Override
  public void destroy() {
    //Use the default behavior
  }

}
