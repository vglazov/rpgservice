package com.example.rpgservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CharacterRepository {

    private LevelUpExpProvider levelUpExpProvider;

    private Map<Integer, CharacterModel> inMemoryRepo = new HashMap<>();
    private Integer nextId = 1;

    @Autowired
    public CharacterRepository(LevelUpExpProvider levelUpExpProvider) {
        this.levelUpExpProvider = levelUpExpProvider;
    }

    public CharacterModel create(String name) {
        Integer id = nextId++;
        CharacterModel character = new CharacterModel(id, name);
        inMemoryRepo.put(id, character);
        return character;
    }

    public CharacterModel getById(Integer id) {
        CharacterModel character = inMemoryRepo.get(id);
        if(character == null) throw new CharacterNotFoundException();
        return character;
    }

    public CharacterModel addExperience(CharacterModel character, Integer experience) {
        synchronized (character) {
            LevelExp newLevelExp = levelUpExpProvider.getUpdatedLevelExp(LevelExp.of(character), experience);
            character.setExperience(newLevelExp.getExp());
            character.setLevel(newLevelExp.getLevel());
        }
        return character;
    }
}
