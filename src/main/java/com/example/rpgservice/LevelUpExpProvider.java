package com.example.rpgservice;

public interface LevelUpExpProvider {
    LevelExp getUpdatedLevelExp(LevelExp old, Integer newExperience);
}
