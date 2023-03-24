package ru.steelbLack.SpringTelegramBot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.steelbLack.SpringTelegramBot.utils.MessageUtils;

@Component
@Log4j2
public class UpdateController {

    private final TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    @Autowired
    public UpdateController(TelegramBot telegramBot, MessageUtils messageUtils) {
        this.telegramBot = telegramBot;
        this.messageUtils = messageUtils;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null");
        }
        if (update.getMessage() != null){
            distributedMessageByType(update);
        }
        else {
            log.error("receive unsupported message type " + update);
        }
    }

    private void distributedMessageByType(Update update) {
        var message = update.getMessage();
        
        if (message.getText() != null){
            processTextMessageUpdate(update);
        }
        else if (message.getDocument() != null){
            processDocMessageUpdate(update);
        }
        else if (message.getPhoto() != null){
            processPhotoMessageUpdate(update);
        }
        else {
            setUnsupportedMessageType(update);
        }
    }

    private void setUnsupportedMessageType(Update update) {
        var sendMessage = messageUtils.generateSendMassage(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.executeMessage(sendMessage);
    }

    private void processPhotoMessageUpdate(Update update) {
    }

    private void processDocMessageUpdate(Update update) {
    }

    private void processTextMessageUpdate(Update update) {
    }
}
