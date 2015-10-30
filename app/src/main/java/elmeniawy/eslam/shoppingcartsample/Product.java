package elmeniawy.eslam.shoppingcartsample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eslam El-Meniawy on 27-Oct-15.
 */
public class Product implements Parcelable {
    private long id;
    private String title;
    private double price;
    private double rating;
    private String image;

    public Product() {
    }

    public Product(long id, String title, double price, double rating, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.rating = rating;
        this.image = image;
    }

    public Product(Parcel parcel) {
        id = parcel.readLong();
        title = parcel.readString();
        price = parcel.readDouble();
        rating = parcel.readDouble();
        image = parcel.readString();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
