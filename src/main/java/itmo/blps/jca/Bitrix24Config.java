package itmo.blps.jca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bitrix24Config {

    @Bean
    public Bitrix24ManagedConnectionFactory bitrix24ManagedConnectionFactory() {
        Bitrix24ManagedConnectionFactory mcf = new Bitrix24ManagedConnectionFactory();
        mcf.setDefaultWebhookUrl("https://b24-qeceqm.bitrix24.ru/rest/1/3mds2b23cikajyyy/");
//        mcf.setDefaultAuthToken("your_auth_token");
        return mcf;
    }

    @Bean
    public Bitrix24ConnectionFactory bitrix24ConnectionFactory(
            Bitrix24ManagedConnectionFactory mcf) {
        return new Bitrix24ConnectionFactory(mcf, null);
    }
}
