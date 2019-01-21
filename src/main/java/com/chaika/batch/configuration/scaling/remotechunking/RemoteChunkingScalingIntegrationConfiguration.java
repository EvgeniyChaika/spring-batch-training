package com.chaika.batch.configuration.scaling.remotechunking;

import com.chaika.batch.utils.dao.Customer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkHandler;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

/**
 * Created by echaika on 21.01.2019
 */
@Configuration
public class RemoteChunkingScalingIntegrationConfiguration {

    private static final String CHUNKING_REQUESTS = "chunking.requests";
    private static final String CHUNKING_REPLIES = "chunking.replies";

    @Bean
    public ChunkHandler remoteChunkingScalingIntegrationChunkHandler(@Qualifier("remoteChunkingScalingJobStep1") TaskletStep step1) throws Exception {
        RemoteChunkHandlerFactoryBean factoryBean = new RemoteChunkHandlerFactoryBean();

        factoryBean.setChunkWriter(remoteChunkingScalingIntegrationChunkWriter());
        factoryBean.setStep(step1);

        return factoryBean.getObject();
    }

    @Bean
    public ChunkMessageChannelItemWriter<Customer> remoteChunkingScalingIntegrationChunkWriter() {
        ChunkMessageChannelItemWriter<Customer> chunkWriter = new ChunkMessageChannelItemWriter<>();

        chunkWriter.setMessagingOperations(remoteChunkingScalingIntegrationMessageTemplate());
        chunkWriter.setReplyChannel(remoteChunkingScalingIntegrationInboundReplies());
        chunkWriter.setMaxWaitTimeouts(10);

        return chunkWriter;
    }

    @Bean
    public MessagingTemplate remoteChunkingScalingIntegrationMessageTemplate() {
        MessagingTemplate messagingTemplate = new MessagingTemplate(remoteChunkingScalingIntegrationOutboundRequests());

        messagingTemplate.setReceiveTimeout(60_000_000L);

        return messagingTemplate;
    }

    @Bean
    @ServiceActivator(inputChannel = "remoteChunkingScalingIntegrationOutboundRequests")
    public AmqpOutboundEndpoint remoteChunkingScalingIntegrationAmqpOutboundEndpoint(AmqpTemplate template) {
        AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);

        endpoint.setExpectReply(false);
        endpoint.setOutputChannel(remoteChunkingScalingIntegrationInboundReplies());
        endpoint.setRoutingKey(CHUNKING_REQUESTS);

        return endpoint;
    }

    @Bean
    public MessageChannel remoteChunkingScalingIntegrationOutboundRequests() {
        return new DirectChannel();
    }

    @Bean
    public Queue remoteChunkingScalingIntegrationRequestQueue() {
        return new Queue(CHUNKING_REQUESTS, false);
    }

    @Bean
    @Profile("slave")
    public AmqpInboundChannelAdapter remoteChunkingScalingIntegrationInboundRequestsAdapter(@Qualifier("remoteChunkingScalingIntegrationRequestContainer") SimpleMessageListenerContainer listenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);

        adapter.setOutputChannel(remoteChunkingScalingIntegrationInboundRequests());
        adapter.afterPropertiesSet();

        return adapter;
    }

    @Bean
    public MessageChannel remoteChunkingScalingIntegrationInboundRequests() {
        return new DirectChannel();
    }

    @Bean
    @Profile("slave")
    @ServiceActivator(inputChannel = "remoteChunkingScalingIntegrationInboundRequests", outputChannel = "remoteChunkingScalingIntegrationOutboundReplies")
    public ChunkProcessorChunkHandler<Customer> remoteChunkingScalingIntegrationChunkProcessorChunkHandler(
            @Qualifier("remoteChunkingScalingJobItemProcessor") ItemProcessor<Customer, Customer> itemProcessor,
            @Qualifier("remoteChunkingScalingJobItemWriter") ItemWriter<Customer> itemWriter) throws Exception {
        SimpleChunkProcessor<Customer, Customer> chunkProcessor = new SimpleChunkProcessor<>(itemProcessor, itemWriter);
        chunkProcessor.afterPropertiesSet();

        ChunkProcessorChunkHandler<Customer> chunkHandler = new ChunkProcessorChunkHandler<>();

        chunkHandler.setChunkProcessor(chunkProcessor);
        chunkHandler.afterPropertiesSet();

        return chunkHandler;
    }

    @Bean
    public QueueChannel remoteChunkingScalingIntegrationOutboundReplies() {
        return new QueueChannel();
    }

    @Bean
    @Profile("slave")
    @ServiceActivator(inputChannel = "remoteChunkingScalingIntegrationOutboundReplies")
    public AmqpOutboundEndpoint remoteChunkingScalingIntegrationAmqpOutboundEndpointReplies(AmqpTemplate template) {
        AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);

        endpoint.setExpectReply(false);
        endpoint.setRoutingKey(CHUNKING_REPLIES);

        return endpoint;
    }

    @Bean
    public Queue remoteChunkingScalingIntegrationReplyQueue() {
        return new Queue(CHUNKING_REPLIES, false);
    }

    @Bean
    public QueueChannel remoteChunkingScalingIntegrationInboundReplies() {
        return new QueueChannel();
    }

    @Bean
    @Profile("master")
    public AmqpInboundChannelAdapter remoteChunkingScalingIntegrationInboundRepliesAdapter(@Qualifier("remoteChunkingScalingIntegrationReplyContainer") SimpleMessageListenerContainer listenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);

        adapter.setOutputChannel(remoteChunkingScalingIntegrationInboundReplies());
        adapter.afterPropertiesSet();

        return adapter;
    }

    @Bean
    @Profile("slave")
    public SimpleMessageListenerContainer remoteChunkingScalingIntegrationRequestContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setQueueNames(CHUNKING_REQUESTS);
        container.setAutoStartup(false);

        return container;
    }

    @Bean
    @Profile("master")
    public SimpleMessageListenerContainer remoteChunkingScalingIntegrationReplyContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setQueueNames(CHUNKING_REPLIES);
        container.setAutoStartup(false);

        return container;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata remoteChunkingScalingIntegrationDefaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();

        pollerMetadata.setTrigger(new PeriodicTrigger(10));

        return pollerMetadata;
    }
}
