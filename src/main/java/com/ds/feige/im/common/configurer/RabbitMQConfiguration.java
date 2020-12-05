package com.ds.feige.im.common.configurer;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
import org.springframework.util.ErrorHandler;

import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.gateway.DiscoveryService;
import com.ds.feige.im.gateway.consumer.DynamicQueueListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableRabbit
@Slf4j
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    private Map<String, Object> getQueueArguments() {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("x-dead-letter-exchange", exchange);
        headers.put("x-dead-letter-routing-key", AMQPConstants.RoutingKeys.PUBLIC_DLX);
        return headers;
    }
    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory, @Autowired DiscoveryService discoveryService) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        Map<String, Object> arguments = getQueueArguments();
        // 动态队列绑定
        for (String queueName : buildDynamicQueueNames(discoveryService)) {
            // 非持久化,排他,链接断开自动删除
            Queue dynamicQueue =
                QueueBuilder.nonDurable(queueName).exclusive().autoDelete().withArguments(arguments).build();
            rabbitAdmin.declareQueue(dynamicQueue);
            Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchange, queueName, null);
            rabbitAdmin.declareBinding(binding);
            log.info("RabbitMQ binding dynamic queue:routingKey={},queue={}", queueName, queueName);
        }
        // 静态队列绑定
        Map<String, String[]> routeKeyQueues = AMQPConstants.BINDINGS;
        for (Map.Entry<String, String[]> entry : routeKeyQueues.entrySet()) {
            for (String queueName : entry.getValue()) {
                // 把死信队列自己排除掉
                QueueBuilder queueBuilder = QueueBuilder.durable(queueName);
                if (!queueName.equals(AMQPConstants.QueueNames.PUBLIC_DLX_DEFAULT)) {
                    queueBuilder.withArguments(arguments);
                }
                rabbitAdmin.declareQueue(queueBuilder.build());
                Binding binding =
                    new Binding(queueName, Binding.DestinationType.QUEUE, this.exchange, entry.getKey(), null);
                rabbitAdmin.declareBinding(binding);
                log.info("RabbitMQ binding static queue:routingKey={},queue={}", entry.getKey(), queueName);
            }
        }
        return rabbitAdmin;
    }

    List<String> buildDynamicQueueNames(DiscoveryService discoveryService) {
        String[] privateQueues = AMQPConstants.ALL_DYNAMIC_QUEUES;
        List<String> result = Lists.newArrayListWithCapacity(privateQueues.length);
        for (String privateQueue : privateQueues) {
            // 声明专属队列
            result.add(privateQueue + discoveryService.getInstanceId());
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
        container.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable t) {
                log.error("Message container error:", t);
            }
        });
        return container;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(this.exchange);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.addBeforePublishPostProcessors(message -> {
            String id = IdUtil.simpleUUID();
            message.getMessageProperties().setMessageId(id);
            return message;
        });
        return rabbitTemplate;
    }
}
