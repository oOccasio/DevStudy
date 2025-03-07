package com.group.libraryapp.controller.assignment;

import com.group.libraryapp.dto.assignment.request.FruitInformationRequest;
import com.group.libraryapp.dto.assignment.request.FruitSaveRequest;
import com.group.libraryapp.dto.assignment.request.FruitUpdateRequest;
import com.group.libraryapp.dto.assignment.response.FruitSellMoney;
import com.group.libraryapp.dto.calculator.request.CalculatorSumRequest;
import com.group.libraryapp.dto.calculator.response.CalculateResponse;
import com.group.libraryapp.dto.calculator.response.DateResponse;
import com.group.libraryapp.service.assignment.FruitService;
import com.group.libraryapp.service.assignment.FruitServiceV2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
public class Assignment2 {

    private final FruitServiceV2 fruitService;


    public Assignment2(FruitServiceV2 fruitService) {
        this.fruitService = fruitService;
    }

    @GetMapping("/api/v1/calc")
    public CalculateResponse calculate(@RequestParam Integer num1, @RequestParam Integer num2) {
        CalculateResponse responses = new CalculateResponse(num1+num2, num1 - num2 , num1 * num2);
        return responses;
    }


    @GetMapping("/api/v1/day-of-the-week")
    public DateResponse dayOfTheWeek(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        return new DateResponse(date.getDayOfWeek());
    }

    @PostMapping("api/v1/sum")
    public Integer sum(@RequestBody CalculatorSumRequest request) {
        Integer sum = 0;
        ArrayList <Integer> numbers = request.getNumbers();


        for(int i = 0 ; i < numbers.size() ; i++){
            sum += numbers.get(i);
        }
        return sum;
    }

    @PostMapping("/api/v1/fruit")
    public void fruitSave(@RequestBody FruitSaveRequest request) {

        fruitService.saveFruit(request);
    }

    @PutMapping("/api/v1/fruit")
    public void fruitUpdate(@RequestBody FruitUpdateRequest request) {

        fruitService.updateFruit(request);
    }



}
