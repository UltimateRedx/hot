package com.hotelpal.service.web.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyHolder extends PropertyPlaceholderConfigurer {
	private static Map<String, String> properties = new ConcurrentHashMap<>();

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		for (Map.Entry<Object, Object> en : props.entrySet()) {
			properties.put(String.valueOf(en.getKey()), String.valueOf(en.getValue()));
		}
	}

	public static String getProperty(String key) {
		return properties.get(key);
	}
}
