package com.example.rpgservice;

public class LevelExp {
    private Integer level;
    private Integer exp;

    public LevelExp(Integer level, Integer exp) {
        this.level = level;
        this.exp = exp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public static LevelExp of(CharacterModel character) {
        return new LevelExp(character.getLevel(), character.getExperience());
    }

}
