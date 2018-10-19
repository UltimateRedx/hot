package com.hotelpal.service.service.spring;

import com.hotelpal.service.service.runonce.RunOnceTasks;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

public class SpringContextListener extends ContextLoaderListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		RunOnceTasks.run();
	}
}
