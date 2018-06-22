package com.ijp.app.drinkshopserver.Retrofit;

import com.ijp.app.drinkshopserver.Model.Category;
import com.ijp.app.drinkshopserver.Model.Drink;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    @GET("getmenu.php")
    Observable<List<Category>> getMenu();

    @FormUrlEncoded
    @POST("DrinkShopServer/Category/add_category.php")
    Observable<String> addNewCategory(@Field("name") String name,@Field("imgPath") String imgPath);

    @Multipart
    @POST("DrinkShopServer/Category/upload_category_img.php")
    Call<String> uploadCategoryFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("DrinkShopServer/Category/update_category.php")
    Observable<String> updateCategory(@Field("id") String id,
                                      @Field("name") String name,
                                      @Field("imgPath") String imgPath);

    @FormUrlEncoded
    @POST("DrinkShopServer/Category/delete_category.php")
    Observable<String> deleteCategory(@Field("id") String id);

    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuid);

    @FormUrlEncoded
    @POST("DrinkShopServer/Product/add_product.php")
    Observable<String> addNewProduct(@Field("name") String name,
                                     @Field("imgPath") String imgPath,
                                     @Field("price") String price,
                                     @Field("menuId") String menuId);

    @Multipart
    @POST("DrinkShopServer/Product/upload_product_img.php")
    Call<String> uploadProductFile(@Part MultipartBody.Part file);





}
