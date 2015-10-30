package elmeniawy.eslam.shoppingcartsample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eslam El-Meniawy on 30-Oct-15.
 */
public class ShoppingCartHelper {
    private static Map<Long, ShoppingCartEntry> cartMap = new HashMap<Long, ShoppingCartEntry>();

    public static void setQuantity(Product product, int quantity) {
        ShoppingCartEntry curEntry = cartMap.get(product.getId());
        if (quantity <= 0) {
            if (curEntry != null)
                removeProduct(product);
            return;
        }
        if (curEntry == null) {
            curEntry = new ShoppingCartEntry(product, quantity);
            cartMap.put(product.getId(), curEntry);
            return;
        }
        curEntry.setQuantity(quantity);
    }

    public static int getProductQuantity(Product product) {
        ShoppingCartEntry curEntry = cartMap.get(product.getId());
        if (curEntry != null)
            return curEntry.getQuantity();
        return 0;
    }

    public static void removeProduct(Product product) {
        cartMap.remove(product.getId());
    }

    public static ArrayList<Product> getCartList() {
        ArrayList<Product> cartList = new ArrayList<>();
        for (ShoppingCartEntry sce : cartMap.values()) {
            cartList.add(sce.getProduct());
        }
        return cartList;
    }
}
