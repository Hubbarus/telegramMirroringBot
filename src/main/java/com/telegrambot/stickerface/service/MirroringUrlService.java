package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
@Getter
public class MirroringUrlService {

    private static final String VALIDATION_REGEX = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w\\-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)";

    private String url;

    private boolean isRegistered;

    protected ConcurrentLinkedQueue<VkMessage> messageQueue = new ConcurrentLinkedQueue<>();

    public String isUrlValid(String urlToValidate) {
        return urlToValidate.matches(VALIDATION_REGEX) ? urlToValidate : null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public synchronized void setRegistered(boolean registered) {
        isRegistered = registered;
    }
}
