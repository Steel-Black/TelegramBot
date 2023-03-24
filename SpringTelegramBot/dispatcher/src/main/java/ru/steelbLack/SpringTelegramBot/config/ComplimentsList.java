package ru.steelbLack.SpringTelegramBot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.steelbLack.SpringTelegramBot.model.Compliment;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class ComplimentsList {

    private List<Compliment> complimentsList;

    public ComplimentsList() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();

        try {
            complimentsList = objectMapper.readValue(new File("db\\compliments.json"), typeFactory.constructCollectionType(List.class, Compliment.class));
        } catch (IOException e) {
            log.error("Error complimentsList " + e.getMessage());
            e.printStackTrace();
        }
    }
}
