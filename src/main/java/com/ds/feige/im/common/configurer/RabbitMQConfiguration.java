package com.ds.feige.im.common.configurer;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.gateway.DiscoveryService;
import com.ds.feige.im.gateway.consumer.DynamicQueueListener;
import com.google.common.collect.Lists;

@Configuration
@EnableRabbit
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory, @Autowired DiscoveryService discoveryService) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 动态队列绑定
        for (String queueName : buildDynamicQueueNames(discoveryService)) {
            // 非持久化,排他,链接断开自动删除
            Queue dynamicQueue = new Queue(queueName, false, true, true);
            rabbitAdmin.declareQueue(dynamicQueue);
            Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchange, queueName, null);
            rabbitAdmin.declareBinding(binding);
            LOGGER.info("RabbitMQ binding dynamic queue:routingKey={},queue={}", queueName, queueName);
        }
        Map<String, String[]> routeKeyQueues = AMQPConstants.BINDINGS;
        for (Map.Entry<String, String[]> entry : routeKeyQueues.entrySet()) {
            for (String queueName : entry.getValue()) {
                Queue queue = new Queue(queueName);
                rabbitAdmin.declareQueue(queue);
                Binding binding =
                    new Binding(queueName, Binding.DestinationType.QUEUE, this.exchange, entry.getKey(), null);
                rabbitAdmin.declareBinding(binding);
                LOGGER.info("RabbitMQ binding static queue:routingKey={},queue={}", entry.getKey(), queueName);
            }

        }
        return rabbitAdmin;
    }

    List<String> buildDynamicQueueNames(DiscoveryService discoveryService) {
        String[] privateQueues = AMQPConstants.ALL_DYNAMIC_QUEUES;
        List<String> result = Lists.newArrayListWithCapacity(privateQueues.length);
        for (int i = 0; i < privateQueues.length; i++) {
            // 声明专属队列
            result.add(privateQueues[i] + discoveryService.getInstanceId());
        }
        return result;
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @ConditionalOnBean(DiscoveryService.class)
    SimpleMessageListenerContainer dynamicMessageListenerContainer(ConnectionFactory connectionFactory,
        @Autowired DiscoveryService discoveryService, @Autowired DynamicQueueListener listener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        List<String> dynamicQueues = buildDynamicQueueNames(discoveryService);
        String[] queueNames = new String[dynamicQueues.size()];
        dynamicQueues.toArray(queueNames);
        container.setQueueNames(queueNames);
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        adapter.setMessageConverter(jackson2JsonMessageConverter());
        container.setMessageListener(adapter);
        return container;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(this.exchange);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
