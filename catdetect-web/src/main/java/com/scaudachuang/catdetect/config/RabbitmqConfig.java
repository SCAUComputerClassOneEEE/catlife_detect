package com.scaudachuang.catdetect.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hiluyx
 * @since 2021/8/31 15:29
 **/
@Configuration
public class RabbitmqConfig {

    public static final String exchange = "cat_detect_exchange";
    public static final String routingKey = "cat_detect_with";
    public static final String queue = "cat_detect_imgs";

    @Bean
    public Queue rabbitmqQueue() {
        return new Queue(queue, false);
    }

    @Bean
    public DirectExchange rabbitmqExchange_direct() {
        return new DirectExchange(exchange, false, true);
    }

    @Bean
    public Binding rabbitmqBinding() {
        return BindingBuilder.bind(rabbitmqQueue())
                .to(rabbitmqExchange_direct())
                .with(routingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
