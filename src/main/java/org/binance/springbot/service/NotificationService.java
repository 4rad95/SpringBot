package org.binance.springbot.service;

import org.binance.springbot.telegramBot.TelegramBot;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final TelegramBot telegramBot; // ← Переменная объявлена здесь

    // Конструктор внедряет бин автоматически
    public NotificationService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // Метод для отправки сообщений
    public void send(String message) {
        Long chatId = 566185151L; // Ваш ID из getUpdates
        telegramBot.sendMessage(chatId, message);
//        Long groupChatId = -100123456789L;
//        telegramBot.sendMessage(groupChatId, message);
    }
}