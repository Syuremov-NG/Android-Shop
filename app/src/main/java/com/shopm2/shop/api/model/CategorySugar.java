package com.shopm2.shop.api.model;

import com.orm.SugarRecord;

public class CategorySugar extends SugarRecord {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    Long id;

    public CategorySugar(){
    }

    public CategorySugar(String name, Long id){
        this.name = name;
        this.id = id;
    }
}
