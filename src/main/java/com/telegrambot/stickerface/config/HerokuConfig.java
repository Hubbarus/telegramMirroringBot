package com.telegrambot.stickerface.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is workaround when deployement on heroku platform
 */
@Configuration
@ConfigurationProperties(prefix = "heroku")
@Deprecated
@Setter
@Slf4j
public class HerokuConfig {
    private String lifecheckUrl;
    private int lifecheckPeriod;
    private int lifecheckDelay;

    @PostConstruct
    public void onStartUp() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> request = new HttpEntity<>("Trigger!");
            try {
                restTemplate.exchange(new URI(lifecheckUrl), HttpMethod.POST, request, String.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }, lifecheckDelay, lifecheckPeriod, TimeUnit.SECONDS);
    }
}
