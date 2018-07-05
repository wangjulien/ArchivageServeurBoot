package com.telino.avp.exception;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import com.telino.avp.service.ExpTaskService;

/**
 * Classe utile Asychrone exception handler
 * 
 * @author Jiliang.WANG
 *
 */
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpTaskService.class);
	
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		LOGGER.error("Exception message - " + ex.getMessage());
		LOGGER.error("Method name - " + method.getName());
        for (Object param : params) {
        	LOGGER.error("Parameter value - " + param);
        }
	}

}
