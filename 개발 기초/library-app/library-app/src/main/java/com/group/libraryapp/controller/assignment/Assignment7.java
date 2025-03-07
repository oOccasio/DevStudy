package com.group.libraryapp.controller.assignment;

import com.group.libraryapp.domain.fruit.Fruit;
import com.group.libraryapp.dto.assignment.response.FruitCount;
import com.group.libraryapp.service.assignment.FruitServiceV2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Assignment7 {

    private final FruitServiceV2 fruitService;

    public Assignment7(FruitServiceV2 fruitService) {
        this.fruitService = fruitService;
    }

    @GetMapping("/api/v1/fruit/count")
    public FruitCount getFruitCount(@RequestParam String name) {
        return fruitService.getFruitCount(name);
    }

    @GetMapping("/api/v1/fruit/list")
    public List<Fruit> getFruits(@RequestParam String option, @RequestParam long price) {
        return fruitService.getFruits(option, price);
    }

}
