package com.telino.avp.service;

import java.util.UUID;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.telino.avp.service" })
public class ConfigTestService {
	
	public static final UUID LOG_EVENT_ID = UUID.randomUUID();
	public static final UUID LOG_ARCHIVE_ID = UUID.randomUUID();
}
