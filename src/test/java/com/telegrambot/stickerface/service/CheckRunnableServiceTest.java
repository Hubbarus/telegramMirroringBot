package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.model.BotUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckRunnableServiceTest {

    @Mock
    private ScheduledExecutorService executorService;

    @Mock
    private MirroringUrlService urlService;

    private CheckRunnableService testService;

    private long chatId = 123455L;
    private BotUser user = new BotUser();

    @BeforeEach
    private void setUp() {
        testService = new CheckRunnableService(chatId, executorService, urlService);
    }

    @Test
    void userRegisteredTest() {
        user.setRegistered(true);
        when(urlService.getBotUserByChatId(chatId)).thenReturn(user);

        testService.run();

        verify(executorService, times(0)).shutdown();
    }

    @Test
    void userIsNotRegisteredTest() {
        user.setRegistered(false);
        when(urlService.getBotUserByChatId(chatId)).thenReturn(user);

        testService.run();

        verify(executorService, times(1)).shutdown();
    }

    @Test
    void userNotFoundTest() {
        when(urlService.getBotUserByChatId(chatId)).thenReturn(null);

        testService.run();

        verify(executorService, times(0)).shutdown();
    }
}