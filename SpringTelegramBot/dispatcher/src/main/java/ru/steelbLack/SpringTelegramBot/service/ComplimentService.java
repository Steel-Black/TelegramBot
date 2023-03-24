package ru.steelbLack.SpringTelegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.steelbLack.SpringTelegramBot.model.Compliment;
import ru.steelbLack.SpringTelegramBot.repositories.ComplimentRepository;


import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ComplimentService {

    private final ComplimentRepository complimentRepository;
    private final Random random = new Random();

    @Autowired
    public ComplimentService(ComplimentRepository complimentRepository) {
        this.complimentRepository = complimentRepository;
    }

    public Compliment getCompliment(){
       List<Compliment> complimentList = complimentRepository.findByIsUsed(false);
       if (complimentList.isEmpty()){
           updateAllCompliments();
           getCompliment();
       }
       Compliment compliment = complimentList.get(random.nextInt(complimentList.size()));
       compliment.setText(compliment.getText().replaceAll("©.+", ""));
       compliment.setUsed(true);
       complimentRepository.save(compliment);
       return compliment;
    }

    private void updateAllCompliments(){
        List<Compliment> compliments = complimentRepository.findAll();
        compliments.forEach(compliment -> compliment.setUsed(false));
    }

    public void saveAll(List<Compliment> compliments){
        complimentRepository.saveAll(compliments);
    }
}
