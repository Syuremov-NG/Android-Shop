package com.shopm2.shop.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryList {
    @SerializedName("media_gallery_entries")
    @Expose
    private List<GalleryDTO> items;

    public List<GalleryDTO> getItems() {
        return items;
    }
}
