package ru.steelbLack.SpringTelegramBot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.steelbLack.SpringTelegramBot.model.Joke;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class JokesList {

    private List<Joke> jokesList;

    public JokesList() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();

        try {
            jokesList = objectMapper.readValue(new File("db/JokeAPI.json"), typeFactory.constructCollectionType(List.class, Joke.class));
        } catch (IOException e) {
            log.error("Error jokeList " + e.getMessage());
            e.printStackTrace();
        }
    }

}
