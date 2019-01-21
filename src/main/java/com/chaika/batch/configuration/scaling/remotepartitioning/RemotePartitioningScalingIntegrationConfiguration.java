package com.chaika.batch.configuration.scaling.remotepartitioning;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.messaging.PollableChannel;

/**
 * Created by echaika on 18.01.2019
 */
@Configuration
public class RemotePartitioningScalingIntegrationConfiguration {

    @Bean
    public MessagingTemplate remotePartitioningScalingJobMessageTemplate() {
        MessagingTemplate messagingTemplate = new MessagingTemplate(remotePartitioningScalingJobOutboundRequests());

        messagingTemplate.setReceiveTimeout(60_000_000L);

        return messagingTemplate;
    }

    @Bean
    public DirectChannel remotePartitioningScalingJobOutboundRequests() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "remotePartitioningScalingJobOutboundRequests")
    public AmqpOutboundEndpoint remotePartitioningScalingJobAmqpOutboundEndpoint(AmqpTemplate template) {
        AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);

        endpoint.setExpectReply(true);
        endpoint.setOutputChannel(remotePartitioningScalingJobInboundRequests());
        endpoint.setRoutingKey("partition.requests");

        return endpoint;
    }


    @Bean
    public Queue remotePartitioningScalingJobRequestQueue() {
        return new Queue("partition.requests", false);
    }

    @Bean
    @Profile("slave")
    public AmqpInboundChannelAdapter remotePartitioningScalingJobInbound(@Qualifier("remotePartitioningScalingJobContainer") SimpleMessageListenerContainer listenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);

        adapter.setOutputChannel(remotePartitioningScalingJobInboundRequests());
        adapter.afterPropertiesSet();

        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer remotePartitioningScalingJobContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setQueueNames("partition.requests");
        container.setAutoStartup(false);

        return container;
    }

    @Bean
    public PollableChannel remotePartitioningScalingJobOutboundStaging() {
        return new NullChannel();
    }

    @Bean
    public QueueChannel remotePartitioningScalingJobInboundRequests() {
        return new QueueChannel();
    }
}
