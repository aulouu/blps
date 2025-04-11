package itmo.blps.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 секунд
        factory.setReadTimeout(5000);    // 5 секунд
        return new RestTemplate(factory);
    }
}
