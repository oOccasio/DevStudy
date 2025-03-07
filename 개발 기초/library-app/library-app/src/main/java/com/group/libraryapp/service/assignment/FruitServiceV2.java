package com.group.libraryapp.service.assignment;

import com.group.libraryapp.domain.fruit.Fruit;
import com.group.libraryapp.dto.assignment.request.FruitSaveRequest;
import com.group.libraryapp.dto.assignment.request.FruitUpdateRequest;
import com.group.libraryapp.dto.assignment.response.FruitCount;
import com.group.libraryapp.repository.assignment.FruitRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
public class FruitServiceV2 {

    private final FruitRepository fruitRepository;

    public FruitServiceV2(FruitRepository fruitRepository) {
        this.fruitRepository = fruitRepository;
    }

    public void saveFruit(FruitSaveRequest request){
        fruitRepository.save(new Fruit(request.getName(), request.getPrice(), request.isSell()));
    }

    public void updateFruit(FruitUpdateRequest request){
        Fruit fruit = fruitRepository.findById(request.getId());
        fruit.updateSell();
    }

    public FruitCount getFruitCount(String name){
        List<Fruit> fruits = fruitRepository.findByName(name);

        long count = fruits.stream()
                            .filter(Fruit::isSell)
                            .count();
        return new FruitCount(count);

    }

    public List<Fruit> getFruits(String option, long price) {
        List<Fruit> fruits = fruitRepository.findAll();
        List<Fruit> filteredFruits = new ArrayList<>();

        filteredFruits = fruits.stream()
                .filter(fruit -> fruit.isSell() == false)  // 판매되지 않은 과일만 필터링
                .filter(fruit -> {
                    if ("GTE".equals(option)) {
                        return fruit.getPrice() >= price;  // 가격이 price 이상
                    } else {
                        return fruit.getPrice() <= price;  // 가격이 price 이하
                    }
                })
                .collect(Collectors.toList());

        return filteredFruits;
    }


}
