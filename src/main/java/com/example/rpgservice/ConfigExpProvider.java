package com.example.rpgservice;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConfigExpProvider implements LevelUpExpProvider {

    private Integer maxLevel;
    private List<Milestone> milestonesSorted;

    @PostConstruct
    public void init() {
        Scanner configScanner = new Scanner(this.getClass().getResourceAsStream("/config.txt"));
        Pattern linePattern = Pattern.compile("^(\\d+)\\|(\\d+|-)$");
        maxLevel = null;
        milestonesSorted = new ArrayList<>();
        Set<Integer> milestoneLevels = new HashSet<>();

        while (configScanner.hasNextLine()) {
            String configLine = configScanner.nextLine();
            Matcher m = linePattern.matcher(configLine);
            if(!m.matches()) {
                throw new IllegalArgumentException("Cannot parse config line: " + configLine);
            }
            Integer level = Integer.parseInt(m.group(1));
            if(level < 1) {
                throw new IllegalArgumentException("Illegal level: " + level);
            }
            if(m.group(2).equals("-")) {
                if(maxLevel != null) {
                    throw new IllegalArgumentException("Max level specified twice");
                }
                maxLevel = level;
            } else {
                if(milestoneLevels.contains(level)) {
                    throw new IllegalArgumentException(String.format("Experience for %d specified twice", level));
                }
                milestoneLevels.add(level);
                int expToLevelUp = Integer.parseInt(m.group(2));
                if(expToLevelUp < 1) {
                    throw new IllegalArgumentException("Illegal experience: " + expToLevelUp);
                }
                milestonesSorted.add(new Milestone(level, expToLevelUp));
            }
        }

        if(maxLevel == null) {
            throw new IllegalArgumentException("Max level is not specified");
        }

        if(milestonesSorted.isEmpty()) {
            throw new IllegalArgumentException("No experience configurations found");
        }

        Collections.sort(milestonesSorted);

        if(milestonesSorted.get(0).startLevel != 1) {
            throw new IllegalArgumentException("Configuration must contain a line for a 1st level experience");
        }

    }

    @Override
    public LevelExp getUpdatedLevelExp(LevelExp old, Integer newExperience) {

        if(newExperience < 0) {
            throw new IllegalArgumentException("newExperience must be positive number");
        }

        LevelExp current = old;
        Integer expToSpend = newExperience;

        while(expToSpend > 0) {
            if(current.getLevel().equals(maxLevel)) {
                current = new LevelExp(current.getLevel(), current.getExp() + expToSpend);
                expToSpend = 0;
            } else {
                Integer expToLevelUp = getExpToLevelUp(current.getLevel()) - current.getExp();
                if(expToSpend >= expToLevelUp) {
                    current = new LevelExp(current.getLevel() + 1, 0);
                    expToSpend = expToSpend - expToLevelUp;
                } else {
                    current = new LevelExp(current.getLevel(), current.getExp() + expToSpend);
                    expToSpend = 0;
                }
            }
        }

        return current;
    }

    private Integer getExpToLevelUp(Integer currentLevel) {
        for(int i = milestonesSorted.size() - 1; i >= 0; i--) {
            if(currentLevel >= milestonesSorted.get(i).startLevel) {
                return milestonesSorted.get(i).expToLevelUp;
            }
        }

        throw new IllegalStateException("Error finding milestone"); // should not happen on valid configuration
    }

    private static class Milestone implements Comparable<Milestone>{
        public Integer startLevel;
        public Integer expToLevelUp;

        public Milestone(Integer startLevel, Integer expToLevelUp) {
            this.startLevel = startLevel;
            this.expToLevelUp = expToLevelUp;
        }

        @Override
        public int compareTo(Milestone that) {
            return this.startLevel.compareTo(that.startLevel);
        }
    }
}
