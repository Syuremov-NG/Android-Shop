package com.shopm2.shop.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String price;
    @SerializedName("media_gallery_entries")
    @Expose
    private List<GalleryDTO> gallery = new ArrayList<GalleryDTO>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GalleryDTO> getGallery() {
        return gallery;
    }

    public void setGallery(List<GalleryDTO> gallery) {
        this.gallery = gallery;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
