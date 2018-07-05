package com.telino.avp.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.telino.avp.dao.paramdao.SystEnvDao;
import com.telino.avp.entitysyst.SystEnv;
import com.telino.avp.service.schedule.ScheduledArchivageAnalysis;

/**
 * Configuration de l'executor pour taches plannifies
 * 
 * @author jwang
 *
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

	public static Map<String, ScheduledFuture<?>> CTE = new ConcurrentHashMap<>();

	@Value("${threadanalysis.cycletime}")
	private int cycleRate;

	@Autowired
	private SystEnvDao systEnvDao;

	@Autowired
	private ScheduledArchivageAnalysis scheduledArchivageAnalysis;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskScheduler());
	}

	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler taskScheduler() {
		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ScheduledTask-");
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
		threadPoolTaskScheduler.initialize();

		// Add the background scheduled tasks for a given App environment
		List<SystEnv> systEnvs = systEnvDao.findAllByBgsOnIsTrue();

		// Associate a identity with each scheduled task
		for (SystEnv env : systEnvs) {
			ScheduledFuture<?> future = threadPoolTaskScheduler.scheduleWithFixedDelay(
					() -> scheduledArchivageAnalysis.launchBackgroudServices(env.getNombase()), cycleRate * 60 * 1000);
			CTE.put(env.getNombase(), future);
		}

		return threadPoolTaskScheduler;
	}
}
