package org.binance.springbot.telegramBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component // ← Обязательно!
public class TelegramBot extends TelegramLongPollingBot {


    private final String BOT_TOKEN = "8192367196:AAHfsXzVEERHGx4kv2YKgkJ6_wdPBWa1wJc";
    private final String BOT_USERNAME = "VSA Radomyr Notify";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // Пример: ответ на команду /start
            if (messageText.equalsIgnoreCase("/start")) {
                sendMessage(chatId, "Привет! Я ваш торговый бот. Готов к работе.");
            }

            // Пример: обработка команды /signal
            if (messageText.startsWith("/signal")) {
                // Вы можете вызвать метод анализа рынка и отправить результат
                String analysisResult = analyzeMarketSignal();
                sendMessage(chatId, analysisResult);
            }
            if ("/test".equalsIgnoreCase(messageText)) {
                sendMessage(chatId, "✅ Бот работает. Получено: " + messageText);
            }
        }
    }

    public int sendMessage(Long chatId, String text) {
        try {
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    BOT_TOKEN, chatId, URLEncoder.encode(text, "UTF-8")
            );

            //    sendHordeImagePost(chatId,"абс", "jj");


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            if (response.statusCode() == 200 && response.body().contains("\"ok\":true")) {
                JsonNode root = new ObjectMapper().readTree(response.body());
                int postId = root.get("result").get("message_id").asInt();
                System.out.println("✅ Пост отправлен с ID: " + postId);
                return postId;
            } else {
                System.err.println("❌ Ошибка при отправке: " + response.body());
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;


//        SendMessage message = new SendMessage();
//        message.setChatId(chatId.toString());
//        message.setText(text);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
        }
    }

    private String analyzeMarketSignal() {
        // Здесь можно подключить ваш модуль анализа сигналов
        return "Сигнал обнаружен: BUY BTCUSDT на уровне 63,500 USDT. Risk/Reward: 1:3.";
    }

}
