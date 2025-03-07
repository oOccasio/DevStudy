package com.group.libraryapp.service.assignment;

import com.group.libraryapp.dto.assignment.request.FruitInformationRequest;
import com.group.libraryapp.dto.assignment.request.FruitUpdateRequest;
import com.group.libraryapp.dto.assignment.response.FruitSellMoney;
import com.group.libraryapp.repository.assignment.FruitJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class FruitService {

    private FruitJdbcRepository fruitJdbcRepository;

    public FruitService(FruitJdbcRepository fruitJdbcRepository) {
        this.fruitJdbcRepository = fruitJdbcRepository;
    }

    public void fruitSave(FruitInformationRequest fruitInformationRequest) {
        fruitJdbcRepository.fruitSave(fruitInformationRequest);
    }
    public void fruitUpdate(FruitUpdateRequest fruitUpdateRequest) {
        fruitJdbcRepository.fruitUpdate(fruitUpdateRequest);
    }

    public FruitSellMoney sellMoney(String name){
        return fruitJdbcRepository.sellMoney(name);
    }


}
