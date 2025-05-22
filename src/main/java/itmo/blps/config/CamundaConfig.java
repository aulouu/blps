package itmo.blps.config;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CamundaConfig {

    private final DataSource camundaDataSource;
    private final JtaTransactionManager jtaTransactionManager;

    @Bean
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

        config.setDataSource(camundaDataSource);
        config.setTransactionManager(jtaTransactionManager);
        config.setDatabaseSchemaUpdate("true");
        config.setJobExecutorActivate(true);

        return config;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }
}
