package ir.galaxycell.kahkeshan.Models;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 04/09/2017.
 */
public class ResSeeProductModel implements Serializable {

    public String productDescription,productPathFile,productDate,productPrice,productUserImage,countLike,productHashtag;
    public boolean flagLike;
    public transient JSONArray productLike=new JSONArray();
    public List<String>listProductLike=new ArrayList<>();

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPathFile() {
        return productPathFile;
    }

    public void setProductPathFile(String productPathFile) {
        this.productPathFile = productPathFile;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public JSONArray getProductLike() {
        return productLike;
    }

    public void setProductLike(JSONArray productLike) {
        this.productLike = productLike;

        for(int i=0;i<productLike.length();i++)
        {
            try
            {
                listProductLike.add(productLike.getString(i));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getListProductLike() {
        return listProductLike;
    }

    public void setListProductLike(List<String> listProductLike) {
        this.listProductLike = listProductLike;
    }

    public String getProductUserImage() {
        return productUserImage;
    }

    public void setProductUserImage(String productUserImage) {
        this.productUserImage = productUserImage;
    }

    public boolean isFlagLike() {
        return flagLike;
    }

    public void setFlagLike(boolean flagLike) {
        this.flagLike = flagLike;
    }

    public String getCountLike() {
        return countLike;
    }

    public void setCountLike(String countLike) {
        this.countLike = countLike;
    }

    public String getProductHashtag() {
        return productHashtag;
    }

    public void setProductHashtag(String productHashtag) {
        this.productHashtag = productHashtag;
    }
}
