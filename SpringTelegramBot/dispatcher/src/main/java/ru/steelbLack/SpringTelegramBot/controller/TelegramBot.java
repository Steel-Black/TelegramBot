package ru.steelbLack.SpringTelegramBot.controller;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.steelbLack.SpringTelegramBot.config.BotConfig;
import ru.steelbLack.SpringTelegramBot.config.ComplimentsList;
import ru.steelbLack.SpringTelegramBot.config.JokesList;
import ru.steelbLack.SpringTelegramBot.model.Joke;
import ru.steelbLack.SpringTelegramBot.model.Status;
import ru.steelbLack.SpringTelegramBot.model.User;
import ru.steelbLack.SpringTelegramBot.service.ComplimentService;
import ru.steelbLack.SpringTelegramBot.service.JokeService;
import ru.steelbLack.SpringTelegramBot.service.UserService;

import java.sql.Statement;
import java.util.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserService userService;
    private final ComplimentService complimentService;
    private final JokeService jokeService;
    private static final String HELP_TEXT = "\"This bot is created to send a random joke from the database each time you request it.\n\n" +
            "You can execute commands from the main menu on the left or by typing commands manually\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /joke to get a random joke\n\n" +
            "Type /status to list available settings to configure\n\n" +
            "Type /help to see this message again\n";

    @Autowired
    public TelegramBot(BotConfig config, UserService userService, JokesList jokesList, ComplimentService complimentService, JokeService jokeService, ComplimentsList complimentsList) {
        this.config = config;
        this.userService = userService;
        this.complimentService = complimentService;
        this.jokeService = jokeService;
        jokeService.saveJokesInDB(jokesList.getJokesList());
        complimentService.saveAll(complimentsList.getComplimentsList());
        this.addBotCommands();
    }
//    @PostConstruct
//    public void init(JokesList jokesList, ComplimentsList complimentsList){
//        jokeService.saveJokesInDB(jokesList.getJokesList());
//        complimentService.saveAll(complimentsList.getComplimentsList());
//        this.addBotCommands();
//    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info(messageText + " update.hasMessage()");

//            if (messageText.contains("/send")  && config.getOwnerId() == chatId){
//                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
//                var users = userRepository.findAll();
//                for (User user: users){
//                    sendMessage(user.getChatId(), textToSend);
//                }
//            } else {

                switch (messageText) {
                    case "/start":
                        userService.registerUser(update.getMessage());
                        showStart(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/joke":
                       chooseCategoryJoke(chatId);
                        break;
                    case "/help":
                        executeMessage(sendMessage(chatId, HELP_TEXT, null));
                        break;
                    case "/status":
                        statusRequest(chatId);
                        break;
                    default:
                        commandNotFound(chatId);
                        break;
//                }
            }
        }
        else if (update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            log.info(callbackData + " update.hasCallbackQuery()");
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.startsWith("Category")) {
                String categoryJoke = callbackData.substring(callbackData.indexOf(" ")).trim();
                Joke joke = jokeService.getJoke(categoryJoke);

                executeMessage(editMessageText(chatId, messageId, joke.getJoke()));
            }
            else if (callbackData.startsWith("Status")) {
                String statusSelect = callbackData.substring(callbackData.indexOf(" ")).trim();
                if (statusSelect.equals("Yes")){
                    var message = editMessageText(chatId, messageId, "Chose status: ");
                    InlineKeyboardMarkup markup = getInlineKeyboardMarkup(List.of(Status.REGULAR_USER.toString(), Status.FAVORITE_USER.toString()), "Request");
                    message.setReplyMarkup(markup);
                    executeMessage(message);
                }
                if (statusSelect.equals("No")){
                    var message = editMessageText(chatId, messageId, "status has not changed");
                    executeMessage(message);
                }

            } else if (callbackData.startsWith("Request")) {
                String statusSelect = callbackData.substring(callbackData.indexOf(" ")).trim();
                userService.changeStatus(chatId,statusSelect);
                String answer = EmojiParser.parseToUnicode(
                        "Complete! :smile:");
                executeMessage(editMessageText(chatId, messageId, answer));

            }
        }
    }


    private void showStart(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode(
                "Hi, " + name + "! :smile:" + " Nice to meet you! I am a Simple Random Joke Bot created by SteelBLack \n");
        executeMessage(sendMessage(chatId, answer, keyBoard()));
    }

    public void statusRequest(long chatId){
        User user = userService.findById(chatId);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(List.of("Yes", "No"), "Status");

        String response = "Ваш статус " + user.getStatus() + "\n" +
        "do u want to change status?";
        var message = sendMessage(chatId, response, null);

        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<String> buttonNames, String callbackData){
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        for (String button:buttonNames){
            var button1 = getButton(button, callbackData + " " + button);
            rowInLine.add(button1);
        }
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    private void chooseCategoryJoke(long chatId) {

        SendMessage message = sendMessage(chatId, "Сhoose a category:", null);

        InlineKeyboardMarkup markupInLine = getInlineKeyboardMarkup(List.of("Programming","Pun","Dark"), "Category");

        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private InlineKeyboardButton getButton(String nameButton, String callBackData){
        var button = new InlineKeyboardButton();

        button.setText(nameButton);
        button.setCallbackData(callBackData);

        return button;
    }



    private ReplyKeyboardMarkup keyBoard(){

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("/help");
        row.add("/joke");
        row.add("/status");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }



    private EditMessageText editMessageText(long chatId, long messageId, String text){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        return message;
    }

    private SendMessage sendMessage(long chatId, String messageToSend, ReplyKeyboardMarkup keyboardMarkup){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageToSend);

        if (keyboardMarkup != null){
            message.setReplyMarkup(keyboardMarkup);
        }
        return message;
    }

    public void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
    public void executeMessage(EditMessageText message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds(){
        var compliment = complimentService.getCompliment();
        var users = userService.findAllByStatus(Status.FAVORITE_USER);

        for (User user : users) {
            String answer = EmojiParser.parseToUnicode("Комплимент для: " + user.getFirstName() + " :smile: \n" +
                    compliment.getText() + " :heart: :heartbeat: ");
            executeMessage(sendMessage(user.getChatId(), answer, null));
        }

    }

    private void commandNotFound(long chatId){
        String answer = EmojiParser.parseToUnicode("Command not recognized, please verify and try again :stuck_out_tongue_winking_eye: ");
        executeMessage(sendMessage(chatId, answer, null));
    }
    private void addBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/joke", "get a random joke"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/status", "get status"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }


    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
