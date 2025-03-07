package com.group.libraryapp.repository.assignment;

import com.group.libraryapp.domain.fruit.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FruitRepository extends JpaRepository<Fruit, Long> {

    public Fruit findById(long id);
    public List<Fruit> findByName(String name);

}
