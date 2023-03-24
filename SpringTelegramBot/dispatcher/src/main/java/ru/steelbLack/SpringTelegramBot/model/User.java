package ru.steelbLack.SpringTelegramBot.model;

import lombok.*;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.RowId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "usersDataTable")
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Setter
@Getter
@NoArgsConstructor
public class User {

    @Id
    private Long chatId;

    private Boolean embedeJoke;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private String userName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime registerAt;

}
