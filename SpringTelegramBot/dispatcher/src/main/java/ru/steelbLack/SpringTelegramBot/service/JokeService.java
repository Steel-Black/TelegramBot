package ru.steelbLack.SpringTelegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.steelbLack.SpringTelegramBot.model.Joke;
import ru.steelbLack.SpringTelegramBot.repositories.JokeRepository;

import java.util.List;
import java.util.Random;
@Service
public class JokeService {

    private final JokeRepository jokeRepository;

    @Autowired
    public JokeService(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
    }

    public void saveJokesInDB(List<Joke> jokeList){
        jokeRepository.saveAll(jokeList);
    }

    public Joke getJoke(String categoryOfJoke){
        Random random = new Random();
        List<Joke> jokeList = jokeRepository.findByCategory(categoryOfJoke).get();
        return jokeList.get(random.nextInt(jokeList.size()));
    }
}
