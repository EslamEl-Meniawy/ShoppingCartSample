package elmeniawy.eslam.shoppingcartsample;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {
    private Product product;
    private TextView itemTitle, itemPrice, itemDetails, inCart;
    private ImageView itemImage;
    private RatingBar itemRating;
    private EditText quantity;
    private Button addToCart;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemTitle = (TextView) findViewById(R.id.ItemTitle);
        itemPrice = (TextView) findViewById(R.id.ItemPrice);
        itemDetails = (TextView) findViewById(R.id.ItemDetails);
        inCart = (TextView) findViewById(R.id.InCart);
        itemImage = (ImageView) findViewById(R.id.ItemImage);
        itemRating = (RatingBar) findViewById(R.id.ItemRating);
        quantity = (EditText) findViewById(R.id.quantity);
        addToCart = (Button) findViewById(R.id.AddToCart);
        Bundle bundle = DetailsActivity.this.getIntent().getExtras();
        if (bundle != null) {
            product = bundle.getParcelable("product");
            itemTitle.setText(product.getTitle());
            if (product.getPrice() > 0) {
                itemPrice.setText("$" + product.getPrice());
            } else {
                itemPrice.setText("Price not available");
            }
            if ((int) product.getRating() == -1) {
                itemRating.setRating(0.0F);
                AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                itemRating.startAnimation(alpha);
            } else {
                itemRating.setRating((float) product.getRating());
            }
            volleySingleton = VolleySingleton.getInstance();
            imageLoader = volleySingleton.getImageLoader();
            String imageName = product.getImage();
            if (imageName != null && !imageName.equals("")) {
                imageLoader.get("http://192.168.0.102:1234/shoppingcart/images/" + imageName, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        itemImage.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }
            requestQueue = volleySingleton.getRequestQueue();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://192.168.0.102:1234/shoppingcart/details.json", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null && response.length() > 0) {
                        if (response.has("details") && !response.isNull("details")) {
                            try {
                                itemDetails.setText(response.getString("details"));
                            } catch (JSONException e) {
                                showSnackBar("Error Parsing Product Details!");
                            }
                        } else {
                            showSnackBar("No Product Details!");
                        }
                    } else {
                        showSnackBar("Error Fetching Product Details!");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showSnackBar("Error Fetching Product Details!");
                }
            });
            requestQueue.add(request);
            inCart.setText(getString(R.string.in_cart) + " " + ShoppingCartHelper.getProductQuantity(product));
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = 0;
                    try {
                        q = Integer.parseInt(quantity.getText().toString());

                        if (q < 0) {
                            showSnackBar("Please enter a quantity of 0 or higher");
                            return;
                        }
                    } catch (Exception e) {
                        showSnackBar("Please enter a numeric quantity");
                        return;
                    }
                    ShoppingCartHelper.setQuantity(product, q);
                    finish();
                }
            });
        } else {
            showSnackBar("Error Fetching Data!");
        }
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
