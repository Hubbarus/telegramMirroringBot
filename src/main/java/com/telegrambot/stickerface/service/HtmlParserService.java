package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HtmlParserService {

    public List<VkMessage> parseHtmlBody(Document document) {
        Elements elements = document.getElementsByAttributeValueStarting("class", "_post_content");
        return elements.stream()
                .map(element -> createVkMessage(element))
                .sorted(Comparator.comparing(VkMessage::getPostDate))
                .collect(Collectors.toList());
    }

    private VkMessage createVkMessage(Element element) {
        VkMessage vkMessage = new VkMessage();
        OffsetDateTime postDate = this.extractPostDate(element);
        vkMessage.setPostDate(postDate);

        element.getElementsByAttributeValueStarting("class", "wall_text").stream()
                .findFirst()
                .ifPresent(el -> {
                    Elements postText = el.getElementsByAttributeValueStarting("class", "wall_post_text");
                    if (postText.size() != 0) {
                        String text = this.extractPostText(postText);
                        vkMessage.setText(text);
                    }

                    Elements postImage = el.getElementsByAttributeValueStarting("class", "page_post");
                    if (postImage.size() != 0) {
                        SendPhoto image = this.extractImage(postImage);
                        vkMessage.setImage(image);
                    }
                });

        return vkMessage;
    }

    private String extractPostText(Elements postText) {

        return postText.stream()
                .findFirst()
                .map(el -> el.text()).orElse("");
    }

    private SendPhoto extractImage(Elements postImage) {
        SendPhoto resultPhoto = new SendPhoto();
        String onclick = postImage.attr("onclick");
        final String regex = "\\{.+\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(onclick);
        if (matcher.find()) {
            String group = matcher.group(0);
            JSONObject jsonObject = new JSONObject(group);
            JSONObject temp = (JSONObject) jsonObject.get("temp");
            String imageUrl = (String) temp.get("x");
            try {
                resultPhoto.setPhoto(readAndConvertImage(imageUrl));
            } catch (IOException ex) {

            }

            System.out.println();
        }

        return resultPhoto;
    }

    private InputFile readAndConvertImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage read = ImageIO.read(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(read, "jpeg", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return new InputFile(is, "image.jpeg");


    }

    private OffsetDateTime extractPostDate(Element element) {
        String dateTime = element.getElementsByAttributeValueStarting("class", "rel_date").stream()
                .findFirst()
                .map(Element::text)
                .orElse("");
        String formattedTime = dateTime.replaceAll("\\D+", "").trim();
        LocalTime time;
        if (formattedTime.contains(":")) {
            time = LocalTime.parse(formattedTime);
        } else if (formattedTime.matches("\\d")) {
            time = LocalTime.now().minusMinutes(Long.parseLong(formattedTime));
        } else {
            time = LocalTime.now();
        }

        return OffsetDateTime.of(LocalDate.now(), time, ZoneOffset.UTC);
    }

}
