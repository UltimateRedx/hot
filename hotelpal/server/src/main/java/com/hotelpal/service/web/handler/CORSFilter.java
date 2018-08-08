package com.hotelpal.service.web.handler;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
		
		}
		
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			HttpServletResponse res = (HttpServletResponse) response;
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Access-Control-Allow-Headers", "Content-Type");
			res.setHeader("Content-Type", "text/plain; charset=UTF-8");
			chain.doFilter(request, response);
		}
		
		@Override
		public void destroy() {
		
		}

}
