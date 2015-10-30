package elmeniawy.eslam.shoppingcartsample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {
    private ArrayList<Product> listProducts = new ArrayList<>();
    private ShoppingCartAdapter shoppingCartAdapter;
    private RecyclerView listProductsRecycler;
    private SwipeRefreshLayout listProductsSwipe;
    private LinearLayoutManager linearLayoutManager;
    public static final String PREF_FILE_NAME = "ShoppingCartPref";
    private FloatingActionButton buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listProductsSwipe = (SwipeRefreshLayout) findViewById(R.id.ListViewCatalogSwipeRefresh);
        listProductsRecycler = (RecyclerView) findViewById(R.id.ListViewCatalog);
        linearLayoutManager = new LinearLayoutManager(ShoppingCartActivity.this);
        listProductsRecycler.setLayoutManager(linearLayoutManager);
        shoppingCartAdapter = new ShoppingCartAdapter(ShoppingCartActivity.this);
        listProductsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCartContent();
            }
        });
        listProductsRecycler.setAdapter(shoppingCartAdapter);
        listProductsSwipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listProductsSwipe.setRefreshing(true);
        getCartContent();
        buy = (FloatingActionButton) findViewById(R.id.ButtonPay);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartHelper.emptyCart();
                finish();
                startActivity(getIntent());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listProductsSwipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listProductsSwipe.setRefreshing(true);
        int oldSize = listProducts.size();
        listProducts = ShoppingCartHelper.getCartList();
        int newSize = listProducts.size();
        if (oldSize != newSize ) {
            finish();
            startActivity(getIntent());
        } else {
            getCartContent();
        }
    }

    private void getCartContent() {
        listProducts = ShoppingCartHelper.getCartList();
        shoppingCartAdapter.setProductsList(listProducts);
        listProductsSwipe.setRefreshing(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        System.out.println(item);
        System.out.println(item.getItemId());
        System.out.println(item.getTitle());
        System.out.println(item.getMenuInfo());
        if (item.getTitle().equals("Delete")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
            builder.setMessage("Delete product from shopping cart?")
                    .setTitle("Delete Product");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences sharedPreferences = ShoppingCartActivity.this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                    Long productid = sharedPreferences.getLong("productid", -1);
                    if (productid != -1) {
                        ShoppingCartHelper.removeProduct(productid);
                        finish();
                        startActivity(getIntent());
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
}
