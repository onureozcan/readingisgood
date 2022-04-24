package com.example.readingisgood.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@EnableJms
@Configuration
public class SqsConfig {

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://localhost:9324", "eu-west-1")
                )
                .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP))
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials("key", "secret")))
                .build();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS()));
        return factory;
    }
}
