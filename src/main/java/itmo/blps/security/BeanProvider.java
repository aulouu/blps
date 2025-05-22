package itmo.blps.security;

import lombok.Setter;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class BeanProvider {
    @Setter
    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public BeanProvider(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }

    @Setter
    private ProcessEngine processEngine = null;

    @Lazy
    @Bean
    public ProcessEngine getProcessEngine() {
        if (processEngine == null) throw new IllegalStateException("Process engine not initialized");
        return processEngine;
    }
}
