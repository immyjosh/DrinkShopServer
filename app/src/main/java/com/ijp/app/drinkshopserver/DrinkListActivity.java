package com.ijp.app.drinkshopserver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ijp.app.drinkshopserver.Adapter.DrinkListAdapter;
import com.ijp.app.drinkshopserver.Model.Drink;
import com.ijp.app.drinkshopserver.Retrofit.IDrinkShopAPI;
import com.ijp.app.drinkshopserver.Utils.Common;
import com.ijp.app.drinkshopserver.Utils.ProgressRequestBody;
import com.ijp.app.drinkshopserver.Utils.UploadCallBack;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrinkListActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1111;
    IDrinkShopAPI mService;
    RecyclerView recyclerDrinks;

    CompositeDisposable compositeDisposable=new CompositeDisposable();

    FloatingActionButton btnAdd;

    ImageView imgBrowser;
    EditText edtDrinkName,edtDrinkPrice;

    Uri selectedUri;
    String uploadedImagePath="";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==PICK_FILE_REQUEST)
            {
                if(data!=null)
                {
                    selectedUri=data.getData();
                    if(selectedUri!=null && !selectedUri.getPath().isEmpty())
                    {
                        imgBrowser.setImageURI(selectedUri);
                        uploadFileToServer();
                    }else {
                        Toast.makeText(this, "Cannot Upload File to Server", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void uploadFileToServer() {
        if(selectedUri!=null)
        {
            File file=FileUtils.getFile(this,selectedUri);

            String fileName=new StringBuilder(UUID.randomUUID().toString())
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile=new ProgressRequestBody(file,this);

            final MultipartBody.Part body=MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadProductFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    uploadedImagePath=new StringBuilder(Common.BASE_URL)
                                            .append("DrinkShopServer/Product/product_image/")
                                            .append(response.body())
                                            .toString();
                                    Log.d("IMGPath",uploadedImagePath);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(DrinkListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_list);

        mService= Common.getAPI();

        btnAdd=findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDrinkDialogue();
            }
        });

        recyclerDrinks=findViewById(R.id.recycler_drink);
        recyclerDrinks.setLayoutManager(new GridLayoutManager(this,2));
        recyclerDrinks.setHasFixedSize(true);

        loadListDrinks(Common.currentCategory.getId());
    }

    private void showAddDrinkDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Add New Product");

        View view= LayoutInflater.from(this).inflate(R.layout.add_new_product_layout,null);

        edtDrinkName=view.findViewById(R.id.edt_drink_name);
        edtDrinkPrice=view.findViewById(R.id.edt_drink_price);
        imgBrowser=view.findViewById(R.id.img_browser);

        imgBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a File"),
                        PICK_FILE_REQUEST);
            }
        });

        builder.setView(view);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                uploadedImagePath="";
                selectedUri=null;

            }
        }).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtDrinkName.getText().toString().isEmpty())
                {
                    Toast.makeText(DrinkListActivity.this, "Please Enter Name Of Product", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtDrinkPrice.getText().toString().isEmpty())
                {
                    Toast.makeText(DrinkListActivity.this, "Please Enter Price Of Product", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(uploadedImagePath.isEmpty())
                {
                    Toast.makeText(DrinkListActivity.this, "Please Select Image Of Product", Toast.LENGTH_SHORT).show();
                    return;
                }
                compositeDisposable.add(mService.addNewProduct(edtDrinkName.getText().toString(),
                        uploadedImagePath,edtDrinkPrice.getText().toString(),
                        Common.currentCategory.ID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(DrinkListActivity.this, s, Toast.LENGTH_SHORT).show();

                        loadListDrinks(Common.currentCategory.getId());
                        
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(DrinkListActivity.this, "internal", Toast.LENGTH_SHORT).show();
                    }
                }));


            }

        }).show();

    }

    private void loadListDrinks(String id) {
        compositeDisposable.add(mService.getDrink(id).observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Drink>>() {
            @Override
            public void accept(List<Drink> drinks) throws Exception {
                displayDrinkList(drinks);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));
    }

    private void displayDrinkList(List<Drink> drinks) {
        DrinkListAdapter adapter=new DrinkListAdapter(this,drinks);
        recyclerDrinks.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        loadListDrinks(Common.currentCategory.getId());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onProgressUpdate(int percentage) {

    }
}
