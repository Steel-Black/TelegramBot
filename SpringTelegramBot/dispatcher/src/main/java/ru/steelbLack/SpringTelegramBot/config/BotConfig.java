package ru.steelbLack.SpringTelegramBot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Data;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
@Data
public class BotConfig {
    @Value("${telegram.botName}")
    private String name;
    @Value("${telegram.botToken}")
    private String token;

    @Value("${telegram.botOwnerId}")
    private Long ownerId;
}
