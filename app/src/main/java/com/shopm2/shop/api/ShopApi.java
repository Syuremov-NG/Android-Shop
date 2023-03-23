package com.shopm2.shop.api;

import com.shopm2.shop.api.body.Auth;
import com.shopm2.shop.api.model.CategoriesList;
import com.shopm2.shop.api.model.CategoryDTO;
import com.shopm2.shop.api.model.ProductsList;
import com.google.gson.JsonPrimitive;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShopApi {

    @GET("natural/all")
    Single<List<CategoryDTO>> getDatesWithPhoto();

    @GET("V1/products?searchCriteria[filterGroups][0][filters][0][field]=category_id")
    Call<ProductsList> getProducts(
            @Header("Authorization") String token,
            @Query("searchCriteria[currentPage]") Integer curPage,
            @Query("searchCriteria[pageSize]") Integer pageSize,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") Integer category
    );

    @GET("V1/categories/list")
    Call<CategoriesList> getCategories(
            @Header("Authorization") String token,
            @Query("searchCriteria[currentPage]") Integer curPage,
            @Query("searchCriteria[pageSize]") Integer pageSize
    );

    @POST("V1/integration/admin/token")
    Call<JsonPrimitive> getToken(@Body Auth auth);
}
