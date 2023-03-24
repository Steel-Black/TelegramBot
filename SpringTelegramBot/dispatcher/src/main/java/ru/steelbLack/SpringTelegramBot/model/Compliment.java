package ru.steelbLack.SpringTelegramBot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "compliment")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Compliment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 1000)
    private String text;
    private boolean isUsed;
}
