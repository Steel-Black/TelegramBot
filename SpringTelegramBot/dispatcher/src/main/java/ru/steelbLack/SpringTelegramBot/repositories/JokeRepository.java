package ru.steelbLack.SpringTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.steelbLack.SpringTelegramBot.model.Joke;

import java.util.List;
import java.util.Optional;

public interface JokeRepository extends JpaRepository<Joke, Long> {
    Optional<List<Joke>> findByCategory(String categoryOfJoke);
}
