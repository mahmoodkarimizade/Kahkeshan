package ir.galaxycell.kahkeshan.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 28/08/2017.
 */
public class ResLoadMoreLibraryAndFavorites implements Serializable {

    public String productId,userId,productImage,productBlock;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductBlock() {
        return productBlock;
    }

    public void setProductBlock(String productBlock) {
        this.productBlock = productBlock;
    }

}
