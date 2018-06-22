package com.ijp.app.drinkshopserver.Utils;

import com.ijp.app.drinkshopserver.Model.Category;
import com.ijp.app.drinkshopserver.Retrofit.IDrinkShopAPI;
import com.ijp.app.drinkshopserver.Retrofit.RetrofitClient;

public class Common {
    public static Category currentCategory;
    public static final String BASE_URL="http://www.thingspeakapi.tk/DrinkShop/";

    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
}
