package com.group.libraryapp.domain.fruit;

import javax.persistence.*;

@Entity
public class Fruit {

    protected Fruit(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false, length = 20)
    private String name;
    private long price;
    private boolean sell;

    public Fruit(String name, long price, boolean sell) {
        this.name = name;
        this.price = price;
        this.sell = sell;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public boolean isSell() {
        return sell;
    }

    public void updateSell(){
        this.sell = true;
    }
}
