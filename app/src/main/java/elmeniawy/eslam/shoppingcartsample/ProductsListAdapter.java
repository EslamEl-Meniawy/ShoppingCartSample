package elmeniawy.eslam.shoppingcartsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Eslam El-Meniawy on 28-Oct-15.
 */
public class ProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static ArrayList<Product> listProducts = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private static Context context;

    public ProductsListAdapter(Context context) {
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
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        viewHolder = new ViewHolderProductsList(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product currentProduct = listProducts.get(position);
        final ViewHolderProductsList holderProduct = (ViewHolderProductsList) holder;
        holderProduct.itemTitle.setText(currentProduct.getTitle());
        if (currentProduct.getPrice() > 0) {
            holderProduct.itemPrice.setText("$" + currentProduct.getPrice());
        } else {
            holderProduct.itemPrice.setText("Price not available");
        }
        if ((int) currentProduct.getRating() == -1) {
            holderProduct.itemRating.setRating(0.0F);
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            holderProduct.itemRating.startAnimation(alpha);
        } else {
            holderProduct.itemRating.setRating((float) currentProduct.getRating());
        }
        String imageName = currentProduct.getImage();
        if (imageName != null && !imageName.equals("")) {
            imageLoader.get("https://shoppingcartsample.herokuapp.com/images/" + imageName, new ImageLoader.ImageListener() {
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

    static class ViewHolderProductsList extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemImage;
        private TextView itemTitle;
        private TextView itemPrice;
        private RatingBar itemRating;

        public ViewHolderProductsList(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.ItemImage);
            itemTitle = (TextView) itemView.findViewById(R.id.ItemTitle);
            itemPrice = (TextView) itemView.findViewById(R.id.ItemPrice);
            itemRating = (RatingBar) itemView.findViewById(R.id.ItemRating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", listProducts.get(getLayoutPosition()));
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }
}
