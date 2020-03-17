package com.example.rpgservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterRepository characters;

    public CharacterController(CharacterRepository characters) {
        this.characters = characters;
    }

    @PostMapping("")
    public CharacterModel newChar(@RequestParam("name") String name) {
        return characters.create(name);
    }

    @GetMapping("/{id}")
    public CharacterModel getById(@PathVariable("id") Integer id) {
        return characters.getById(id);
    }

    @PostMapping("/{id}/add-experience")
    public CharacterModel addExperience(@PathVariable("id") Integer id, @RequestParam("exp") Integer exp) {
        return characters.addExperience(characters.getById(id), exp);
    }

}
