package ru.steelbLack.SpringTelegramBot.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.steelbLack.SpringTelegramBot.model.Status;
import ru.steelbLack.SpringTelegramBot.model.User;
import ru.steelbLack.SpringTelegramBot.repositories.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = User.builder()
                    .chatId(chatId)
                    .firstName(chat.getFirstName())
                    .lastName(chat.getLastName())
                    .userName(chat.getUserName())
                    .status(Status.REGULAR_USER)
                    .build();

            userRepository.save(user);
            log.info("user " + user.getFirstName() + " saved");
        }
    }

    public List<User> findAllByStatus(Status status) {
       Optional<List<User>> users = userRepository.findAllByStatus(status);
        if (users.isEmpty()){
            return Collections.emptyList();
        }
        return users.get();
    }

    public User findById(long chatId) {
        return userRepository.findById(chatId).get();
    }

    public void changeStatus(long chatId, String statusSelect) {
        User user  = userRepository.findById(chatId).get();
        user.setStatus(Status.valueOf(statusSelect));
        userRepository.save(user);
    }
}
