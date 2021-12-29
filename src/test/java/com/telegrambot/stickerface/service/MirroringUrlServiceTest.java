package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.repository.BotUserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MirroringUrlServiceTest {

    @Mock
    private BotUserRepository repository;

    private MirroringUrlService service;

    private static Stream<Arguments> provideUrlData() {
        return Stream.of(
                Arguments.of("https://vk.com/fknsvd", true),
                Arguments.of("http://vk.com/fknsvd", true),
                Arguments.of("https//vk.com/fknsvd", false),
                Arguments.of("https://vk,com/fknsvd", false),
                Arguments.of("htt:vk!com/fknsvd", false)
        );
    }

    @ParameterizedTest
    @MethodSource(value = "provideUrlData")
    void urlValidationTests(String url, boolean isValid) {
        service = new MirroringUrlService(repository);
        assertEquals(isValid, service.isUrlValid(url));
    }

}