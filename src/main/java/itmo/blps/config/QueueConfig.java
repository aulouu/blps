package itmo.blps.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public Queue orderConfirmationQueue() {
        return new ActiveMQQueue("order.confirmation.queue");
    }
}
