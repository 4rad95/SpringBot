package org.binance.springbot.telegramBot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String analyzeMarketSignal() {
        // Здесь можно подключить ваш модуль анализа сигналов
        return "Сигнал обнаружен: BUY BTCUSDT на уровне 63,500 USDT. Risk/Reward: 1:3.";
    }

}
