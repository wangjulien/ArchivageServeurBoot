package com.telino.avp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"com.telino.avp.servlets"})
@EnableWebMvc
@Import(AppSpringConfig.class)
public class WebConfig implements WebMvcConfigurer {
	 @Override
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	
	 }
}
