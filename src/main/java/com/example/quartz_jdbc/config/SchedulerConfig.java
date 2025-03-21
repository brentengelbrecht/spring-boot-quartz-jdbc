package com.example.quartz_jdbc.config;

import com.example.quartz_jdbc.service.MessageService;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.simpl.SimpleJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class SchedulerConfig {

    @Autowired
    private DatasourceProperties datasourceProperties;

    @Autowired
    private QuartzProperties quartzProperties;

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(datasourceProperties.getDriverClassName());
        dataSourceBuilder.url(datasourceProperties.getUrl());
        dataSourceBuilder.username(datasourceProperties.getUsername());
        dataSourceBuilder.password(datasourceProperties.getPassword());
        return dataSourceBuilder.build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(properties);
        factory.setJobFactory(new SimpleJobFactory());
        return factory;
    }

    @Bean
    public JobDetail messageJobDetail() {
        return JobBuilder.newJob(MessageService.class)
                .storeDurably()
                .withIdentity("Message_Job_Detail")
                .withDescription("Invoke MessageService Job...")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Message_Trigger")
                .withDescription("Seconds trigger")
                .withSchedule(simpleSchedule().withIntervalInSeconds(5).withRepeatCount(10))
                .build();
    }

    @Bean()
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(trigger(messageJobDetail()));
        scheduler.start();
        return scheduler;
    }
}
