package com.telegrambot.stickerface.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vk")
@Getter
@Setter
public class VkClientConfig {

    private Integer appId;

    private String redirectUri;

    private String tokenUrl;

    private int waitingLoginTime;

    @Bean
    public VkApiClient getVkApiCLient(TransportClient transportClient) {
        return new VkApiClient(transportClient);
    }

    @Bean
    public TransportClient getTransportClient() {
        return new HttpTransportClient();
    }
}