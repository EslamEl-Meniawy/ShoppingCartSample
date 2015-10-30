package elmeniawy.eslam.shoppingcartsample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String PREF_FILE_NAME = "ShoppingCartPref";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ArrayList<Product> listProducts = new ArrayList<>();
    private ProductsListAdapter productsListAdapter;
    private RecyclerView listProductsRecycler;
    private SwipeRefreshLayout listProductsSwipe;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton viewShoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = MainActivity.this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        listProductsSwipe = (SwipeRefreshLayout) findViewById(R.id.ListViewCatalogSwipeRefresh);
        listProductsRecycler = (RecyclerView) findViewById(R.id.ListViewCatalog);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        listProductsRecycler.setLayoutManager(linearLayoutManager);
        productsListAdapter = new ProductsListAdapter(MainActivity.this);
        listProductsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendJSONRequest();
            }
        });
        listProductsRecycler.setAdapter(productsListAdapter);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        listProductsSwipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listProductsSwipe.setRefreshing(true);
        sendJSONRequest();
        viewShoppingCart = (FloatingActionButton) findViewById(R.id.ButtonViewCart);
        viewShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShoppingCartActivity.class));
            }
        });
    }

    private void sendJSONRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://192.168.0.102:1234/shoppingcart/list.json", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("productsList", response.toString());
                editor.apply();
                listProducts = parseJSONResponse(response);
                productsListAdapter.setProductsList(listProducts);
                listProductsSwipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listProductsSwipe.setRefreshing(false);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    showSnackBar("No Internet connection!");
                } else {
                    showSnackBar("Error Fetching Data!");
                }
                String strJson = sharedPreferences.getString("productsList", "");
                if (!strJson.equals("")) {
                    try {
                        JSONObject jsonData = new JSONObject(strJson);
                        listProducts = parseJSONResponse(jsonData);
                        productsListAdapter.setProductsList(listProducts);
                    } catch (JSONException e) {
                    }
                }
            }
        });
        requestQueue.add(request);
    }

    private ArrayList<Product> parseJSONResponse(JSONObject response) {
        ArrayList<Product> listProducts = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                if (response.has("result")) {
                    JSONArray arrayProducts = response.getJSONArray("result");
                    for (int i = 0; i < arrayProducts.length(); i++) {
                        JSONObject currentProduct = arrayProducts.getJSONObject(i);
                        long id = -1;
                        if (currentProduct.has("id") && !currentProduct.isNull("id")) {
                            id = currentProduct.getLong("id");
                        }
                        String title = "No Data Available";
                        if (currentProduct.has("title") && !currentProduct.isNull("title")) {
                            title = currentProduct.getString("title");
                        }
                        double price = 00.00;
                        if (currentProduct.has("price") && !currentProduct.isNull("price")) {
                            price = currentProduct.getDouble("price");
                        }
                        double rating = -1;
                        if (currentProduct.has("rating") && !currentProduct.isNull("rating")) {
                            rating = currentProduct.getDouble("rating");
                        }
                        String image = null;
                        if (currentProduct.has("image") && !currentProduct.isNull("image")) {
                            image = currentProduct.getString("image");
                        }
                        Product product = new Product();
                        product.setId(id);
                        product.setTitle(title);
                        product.setPrice(price);
                        product.setRating(rating);
                        product.setImage(image);
                        if (id != -1 && !title.equals("No Data Available")) {
                            listProducts.add(product);
                        }
                    }
                } else {
                    showSnackBar("No data received!");
                    String strJson = sharedPreferences.getString("productsList", "");
                    if (!strJson.equals("")) {
                        try {
                            JSONObject jsonData = new JSONObject(strJson);
                            listProducts = parseJSONResponse(jsonData);
                            productsListAdapter.setProductsList(listProducts);
                        } catch (JSONException e) {
                        }
                    }
                }
            } catch (JSONException e) {
                showSnackBar(e.toString());
                String strJson = sharedPreferences.getString("productsList", "");
                if (!strJson.equals("")) {
                    try {
                        JSONObject jsonData = new JSONObject(strJson);
                        listProducts = parseJSONResponse(jsonData);
                        productsListAdapter.setProductsList(listProducts);
                    } catch (JSONException ex) {
                    }
                }
            }
        }
        return listProducts;
    }

    private void showSnackBar(String message) {
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}
