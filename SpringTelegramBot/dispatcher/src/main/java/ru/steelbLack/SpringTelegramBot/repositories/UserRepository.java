package ru.steelbLack.SpringTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.steelbLack.SpringTelegramBot.model.Status;
import ru.steelbLack.SpringTelegramBot.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<List<User>> findAllByStatus(Status status);
}
