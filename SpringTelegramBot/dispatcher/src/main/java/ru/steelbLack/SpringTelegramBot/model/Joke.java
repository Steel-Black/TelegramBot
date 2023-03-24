package ru.steelbLack.SpringTelegramBot.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "jokeTable")
@Data
public class Joke {

    @Id
    private Long id;

    @Column(length = 1000)
    private String joke;

    private String category;

}
