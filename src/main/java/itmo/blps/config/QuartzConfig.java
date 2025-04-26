package itmo.blps.config;

import itmo.blps.job.CleanupSessionJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
@Slf4j
public class QuartzConfig {

    private final ApplicationContext applicationContext;
    @Value("${job.session-cleanup}")
    private int intervalInMinutes;

    public QuartzConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail jobDetail) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(jobDetail);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public JobDetail cleanupSessionJobDetail() {
        return JobBuilder.newJob(CleanupSessionJob.class)
                .withIdentity("cleanupSessionJob", "session-jobs")
                .withDescription("Periodically cleanup active session")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger updateUserSegmentsTrigger(JobDetail jobDetail) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(intervalInMinutes)
                .repeatForever();

        log.info("Configuring Quartz trigger for job '{}' with SimpleSchedule: interval 10 minutes, repeat forever.", jobDetail.getKey().getName());

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("CleanupSessionTrigger", "session-triggers")
                .withDescription("Trigger for cleanup session")
                .withSchedule(scheduleBuilder)
                .startNow()
                .build();
    }

    private static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
        private ApplicationContext ctx;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.ctx = applicationContext;
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            ctx.getAutowireCapableBeanFactory().autowireBean(job);
            return job;
        }
    }
}
