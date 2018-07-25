package com.telino.avp.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.telino.avp.exception.CustomAsyncExceptionHandler;

/**
 * Configuration pour l'executor Asychrone de Spring 
 * 
 * @author jwang
 *
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {
	
	@Value("${app.asnytask.max-thread-pool:5}")
	private int maxThreadPool;
	
	@Override
	public Executor getAsyncExecutor() {
		return taskExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new CustomAsyncExceptionHandler();
	}
	
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(maxThreadPool);
		executor.setThreadNamePrefix("ExpTaskLauncher-");
		executor.setWaitForTasksToCompleteOnShutdown(false);
		executor.initialize();
		return executor;
	}
	
	@Bean(destroyMethod = "shutdown")
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(maxThreadPool);
	}

}
