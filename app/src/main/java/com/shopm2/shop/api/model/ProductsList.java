package com.shopm2.shop.api.model;

import java.util.List;

public class ProductsList {
    private List<ProductDTO> items;

    public List<ProductDTO> getItems() {
        return items;
    }

    public void setItems(List<ProductDTO> items) {
        this.items = items;
    }
}
