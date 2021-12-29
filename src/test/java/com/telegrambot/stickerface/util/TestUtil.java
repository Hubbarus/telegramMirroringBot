package com.telegrambot.stickerface.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class TestUtil {

    private static final String TEST_DATA_PATH = "src/test/resources";

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static String loadJsonStringFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(TEST_DATA_PATH, filePath)));
    }

    public static <T> T loadFromFile(String filePath, Class<T> type) throws IOException {
        return objectMapper.readValue(loadJsonStringFromFile(filePath), type);
    }

    public static <T, C extends Collection<T>> C loadCollectionFromFile(String fileName, Class<? extends Collection> collectionType, Class<T> type) throws IOException {
        CollectionType onCollectionType = objectMapper.getTypeFactory().constructCollectionType(collectionType, type);
        return objectMapper.readValue(loadJsonStringFromFile(fileName), onCollectionType);
    }


}
