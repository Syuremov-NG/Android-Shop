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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopm2.shop.api.ShopService;
import com.shopm2.shop.api.body.Auth;
import com.shopm2.shop.api.model.ProductDTO;
import com.shopm2.shop.api.model.ProductsList;
import com.google.gson.JsonPrimitive;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoListActivity extends AppCompatActivity {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin0000";
    private String token;
    private ImageView imageView;

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

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
        switch (item.getItemId()) {
            case R.id.back:
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                this.startActivity(intent);
            case R.id.exit:
                finish();
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void start(Context caller, String id, String name) {
        Intent intent = new Intent(caller, PhotoListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        caller.startActivity(intent);
    }

    private void getToken() {
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
                        getProducts();
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
                            finishAffinity();
                            System.exit(0);
                        });
                        AlertDialog dialog = alt.create();
                        dialog.setTitle("Error");
                        dialog.show();
                        Toast.makeText(PhotoListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle arguments = getIntent().getExtras();

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        Intent intent = getIntent();

        getSupportActionBar().setTitle(intent.getStringExtra("name"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        App app = (App) getApplication();

        getToken();
//        disposable.add(app.getNasaService().getShopApi().getProducts(token, 1, 50)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new BiConsumer<List<ProductDTO>, Throwable>() {
//                    @Override
//                    public void accept(List<ProductDTO> products, Throwable throwable) throws Exception {
//                        if (throwable != null) {
//                            Toast.makeText(PhotoListActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
//                        } else {
//                            adapter.setProducts(products);
//                        }
//                    }
//                }));
    }

    private void getProducts() {
        Auth auth = new Auth(USERNAME, PASSWORD);
        Context context = this;
        Intent intent = getIntent();
        ShopService.getInstance()
                .getShopApi()
                .getProducts(token, 1, 50, Integer.valueOf(intent.getStringExtra("id")))
                .enqueue(new Callback<ProductsList>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductsList> call, @NonNull Response<ProductsList> response) {
                        assert response.body() != null;
                        adapter.setProducts(response.body().getItems());
                    }
                    @Override
                    public void onFailure(@NonNull Call<ProductsList> call, @NonNull Throwable t) {
                        Log.d("TAG","response.raw().request().url();"+t.getLocalizedMessage());
                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setMessage(t.getMessage()).setPositiveButton("Try Again", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        });
                        alt.setMessage(t.getMessage()).setNegativeButton("Exit", (dialogInterface, i) -> {
                            finishAffinity();
                            System.exit(0);
                        });
                        AlertDialog dialog = alt.create();
                        dialog.setTitle("Error");
                        dialog.show();
                        Toast.makeText(PhotoListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private static class Adapter extends RecyclerView.Adapter<PhotoItemViewHolder> {

        private ArrayList<ProductDTO> products = new ArrayList<>();

        public void setProducts(List<ProductDTO> products) {
            this.products.clear();
            this.products.addAll(products);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PhotoItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PhotoItemViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_photo, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoItemViewHolder photoItemViewHolder, int i) {
            Picasso.get().load(
                    "http://192.168.125.8:80/pub/media/catalog/product/"
                    +products.get(i).getGallery().get(0).getFile()
            ).into(photoItemViewHolder.image);
            photoItemViewHolder.text.setText(products.get(i).getName());
            photoItemViewHolder.price.setText(products.get(i).getPrice() + "$");
            photoItemViewHolder.product = products.get(i);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }

    private static class PhotoItemViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView price;
        ImageView image;
        ProductDTO product;

        public PhotoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.product_text);
            price = itemView.findViewById(R.id.product_price);
            image = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoActivity.start(
                            view.getContext(),
                            "http://192.168.125.8:80/pub/media/catalog/product/"
                                    +product.getGallery().get(0).getFile()
                    );
                }
            });
        }

//        public void bind(ProductDTO product) {
//            this.product = product;
//        }

//        public PhotoItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            text = (TextView) itemView.findViewById(R.id.text);
//        }
//

    }
}
