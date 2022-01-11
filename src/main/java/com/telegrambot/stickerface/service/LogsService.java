package com.telegrambot.stickerface.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LogsService {

    public SendDocument getLogs(int hours, String chatId) throws IOException {
        return SendDocument.builder()
                .chatId(chatId)
                .document(getInputFile(hours)).build();
    }

    private InputFile getInputFile(int hours) throws IOException {
        InputFile inputFile = new InputFile();

        LocalDateTime requestedDateTime = LocalDateTime.now().minusHours(hours);

        Collection<File> allLogFiles = getAllLogFiles(requestedDateTime);

        File zipFile = new File("mirroring_logs_from_".concat(requestedDateTime.toString()).concat(".zip"));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        for (File file : allLogFiles) {
            ZipEntry e = new ZipEntry(file.getName());
            out.putNextEntry(e);

            byte[] data = FileUtils.readFileToByteArray(file);
            out.write(data, 0, data.length);
            out.closeEntry();
        }
        out.close();

        inputFile.setMedia(zipFile);
        return inputFile;
    }

    private Collection<File> getAllLogFiles(LocalDateTime requestedDateTime) {
        Path path = Paths.get("/logs");
        return FileUtils.listFiles(path.iterator().next().toFile(), new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return isLogDateAfterNeeded(file.getName(), requestedDateTime);
            }

            @Override
            public boolean accept(File file, String s) {
                return isLogDateAfterNeeded(new File(file, s).getName(), requestedDateTime);
            }
        }, null);
    }

    private boolean isLogDateAfterNeeded(String fileName, LocalDateTime requestedDateTime) {
        final String regex = "(-)(.+[^.][^.log])";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            String dateString = matcher.group(2);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
            LocalDateTime logDateTime = LocalDateTime.parse(dateString, dateTimeFormatter);
            return logDateTime.isAfter(requestedDateTime);
        } else return false;
    }
}
