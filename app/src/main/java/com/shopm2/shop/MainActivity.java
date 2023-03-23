package com.shopm2.shop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.shopm2.shop.api.ShopService;
import com.shopm2.shop.api.body.Auth;
import com.shopm2.shop.api.model.CategoriesList;
import com.shopm2.shop.api.model.CategoryDTO;
import com.google.gson.JsonPrimitive;
import com.shopm2.shop.api.model.CategorySugar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    private String token;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin0000";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.exit) {
            finish();
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SugarContext.init(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        Context context = this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getSupportActionBar().setTitle(getString(R.string.choose_category));
        App app = (App) getApplication();

        getToken();
    }

    private void getToken() {
        List<Observable<?>> requests = new ArrayList<>();
        Auth auth = new Auth(USERNAME, PASSWORD);
        Context context = this;

        ShopService.getInstance()
                .getShopApi()
                .getToken(auth)
                .enqueue(new Callback<JsonPrimitive>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonPrimitive> call, @NonNull Response<JsonPrimitive> response) {
                        assert response.body() != null;
                        token = "Bearer " + response.body().toString().replaceAll("^\"|\"$", "");
                        getCategories();
                    }
                    @Override
                    public void onFailure(@NonNull Call<JsonPrimitive> call, @NonNull Throwable t) {
                        Log.d("TAG","response.raw().request().url();"+t.getLocalizedMessage());
                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setMessage(t.getMessage()).setPositiveButton("Try Again", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        });
                        alt.setMessage(t.getMessage()).setNegativeButton("Exit", (dialogInterface, i) -> {
                            finish();
                            System.exit(0);
                        });
                        AlertDialog dialog = alt.create();
                        dialog.setTitle("Error");
                        dialog.show();
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getCategories() {
        Auth auth = new Auth(USERNAME, PASSWORD);
        Context context = this;

        ShopService.getInstance()
                .getShopApi()
                .getCategories(token, 1, 50)
                .enqueue(new Callback<CategoriesList>() {
                    @Override
                    public void onResponse(@NonNull Call<CategoriesList> call, @NonNull Response<CategoriesList> response) {
                        if (response.body() != null) {
                            adapter.setCategories(response.body().getItems());
                            return;
                        }
                        List<CategorySugar> list = CategorySugar.listAll(CategorySugar.class);
                        adapter.setCacheCategories(list);
                    }
                    @Override
                    public void onFailure(@NonNull Call<CategoriesList> call, @NonNull Throwable t) {
//                        Log.d("TAG","response.raw().request().url();"+t.getLocalizedMessage());
//                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
//                        alt.setMessage(t.getMessage()).setPositiveButton("Try Again", (dialogInterface, i) -> {
//                            dialogInterface.cancel();
//                            Intent intent = new Intent(context, MainActivity.class);
//                            startActivity(intent);
//                        });
//                        alt.setMessage(t.getMessage()).setNegativeButton("Exit", (dialogInterface, i) -> {
//                            finishAffinity();
//                            System.exit(0);
//                        });
//                        AlertDialog dialog = alt.create();
//                        dialog.setTitle("Error");
//                        dialog.show();
//                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        adapter.setCacheCategories(CategorySugar.listAll(CategorySugar.class));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<CategoryDTO> categories = new ArrayList<>();
        ArrayList<CategorySugar> categoryCache = new ArrayList<>();
        public void setCategories(List<CategoryDTO> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
            CategorySugar.deleteAll(CategorySugar.class);
            for (CategoryDTO category: categories) {
                CategorySugar cache = new CategorySugar(category.getName(), Long.valueOf(category.getId()));
                categoryCache.add(cache);
                cache.save();
            }
            notifyDataSetChanged();
        }

        public void setCacheCategories(List<CategorySugar> items) {
            this.categoryCache.clear();
            this.categoryCache.addAll(items);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(
                    LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_date, viewGroup, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            if (!categories.isEmpty()) {
                viewHolder.bind(categories.get(i));
            } else {
                viewHolder.bindCache(categoryCache.get(i));
            }

        }

        @Override
        public int getItemCount() {
            if (categories.size() == 0) {
                return categoryCache.size();
            }
            return categories.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        CategoryDTO categoryDTO;
        CategorySugar categoryCache;

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.category_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoListActivity.start(view.getContext(), categoryDTO.getId(), categoryDTO.getName());
                }
            });
        }

        public void bind(CategoryDTO categories) {
            categoryDTO = categories;
            text.setText(categories.getName());
        }

        public void bindCache(CategorySugar categories) {
            categoryCache = categories;
            text.setText(categories.getName());
        }
    }
}
