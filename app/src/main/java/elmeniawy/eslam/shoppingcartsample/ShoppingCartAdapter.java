package elmeniawy.eslam.shoppingcartsample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Eslam El-Meniawy on 30-Oct-15.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static ArrayList<Product> listProducts = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private static Context context;
    public static final String PREF_FILE_NAME = "ShoppingCartPref";

    public ShoppingCartAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        this.context = context;
    }

    public void setProductsList(ArrayList<Product> listProducts) {
        this.listProducts = listProducts;
        notifyItemRangeChanged(0, listProducts.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = layoutInflater.inflate(R.layout.cart_item, parent, false);
        viewHolder = new ViewHolderProductsList(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product currentProduct = listProducts.get(position);
        final ViewHolderProductsList holderProduct = (ViewHolderProductsList) holder;
        holderProduct.itemTitle.setText(currentProduct.getTitle());
        holderProduct.itemQuantity.setText("Quantity: " + ShoppingCartHelper.getProductQuantity(currentProduct));
        if (currentProduct.getPrice() > 0) {
            holderProduct.itemPrice.setText("Total price: $" + currentProduct.getPrice() * ShoppingCartHelper.getProductQuantity(currentProduct));
        } else {
            holderProduct.itemPrice.setText("Price not available");
        }
        String imageName = currentProduct.getImage();
        if (imageName != null && !imageName.equals("")) {
            imageLoader.get("http://192.168.0.102:1234/shoppingcart/images/" + imageName, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holderProduct.itemImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    static class ViewHolderProductsList extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private ImageView itemImage;
        private TextView itemTitle;
        private TextView itemQuantity;
        private TextView itemPrice;

        public ViewHolderProductsList(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.ItemImage);
            itemTitle = (TextView) itemView.findViewById(R.id.ItemTitle);
            itemQuantity = (TextView) itemView.findViewById(R.id.ItemQuantity);
            itemPrice = (TextView) itemView.findViewById(R.id.ItemPrice);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", listProducts.get(getLayoutPosition()));
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            menu.add(0, v.getId(), 0, "Delete");
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("productid", listProducts.get(getLayoutPosition()).getId());
            editor.apply();
        }
    }
}
