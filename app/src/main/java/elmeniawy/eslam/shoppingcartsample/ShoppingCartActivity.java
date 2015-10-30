package elmeniawy.eslam.shoppingcartsample;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {
    private ArrayList<Product> listProducts = new ArrayList<>();
    private ShoppingCartAdapter shoppingCartAdapter;
    private RecyclerView listProductsRecycler;
    private SwipeRefreshLayout listProductsSwipe;
    private LinearLayoutManager linearLayoutManager;

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
}
