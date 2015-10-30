package elmeniawy.eslam.shoppingcartsample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eslam El-Meniawy on 30-Oct-15.
 */
public class ShoppingCartHelper {
    private static Map<Product, ShoppingCartEntry> cartMap = new HashMap<Product, ShoppingCartEntry>();
    public static void setQuantity(Product product, int quantity) {
        ShoppingCartEntry curEntry = cartMap.get(product);
        if (quantity <= 0) {
            if (curEntry != null)
                removeProduct(product);
            return;
        }
        if (curEntry == null) {
            curEntry = new ShoppingCartEntry(product, quantity);
            cartMap.put(product, curEntry);
            return;
        }
        curEntry.setQuantity(quantity);
    }

    public static int getProductQuantity(Product product) {
        ShoppingCartEntry curEntry = cartMap.get(product);
        if (curEntry != null)
            return curEntry.getQuantity();
        return 0;
    }

    public static void removeProduct(Product product) {
        cartMap.remove(product);
    }

    public static ArrayList<Product> getCartList() {
        ArrayList<Product> cartList = new ArrayList<>();
        for (Product p : cartMap.keySet()) {
            cartList.add(p);
        }
        return cartList;
    }
}
