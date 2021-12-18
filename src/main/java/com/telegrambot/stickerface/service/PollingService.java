package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class PollingService implements Runnable {

    @Autowired
    private MirroringUrlService urlService;

    @Autowired
    private HtmlParserService parserService;

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public void run() {
        log.info("Requesting host...\n");
        Connection connection = Jsoup.connect(urlService.getUrl());
        int responseCode = connection.execute().statusCode();
        log.info("Polled! Code: " + responseCode);
        if (responseCode == 200) {
            Document document = connection.get();
            List<VkMessage> vkMessages = parserService.parseHtmlBody(document);
            for (VkMessage message : vkMessages) {
                urlService.getMessageQueue().add(message);
            }
        } else {
            log.error("Invalid response from server: " + responseCode);
        }
    }
}
