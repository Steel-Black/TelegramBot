package ru.steelbLack.SpringTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.steelbLack.SpringTelegramBot.model.Compliment;

import java.util.List;
import java.util.Optional;

public interface ComplimentRepository extends JpaRepository<Compliment, Long> {

    List<Compliment> findByIsUsed(boolean isUsed);
}
