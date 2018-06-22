package com.ijp.app.drinkshopserver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ijp.app.drinkshopserver.Retrofit.IDrinkShopAPI;
import com.ijp.app.drinkshopserver.Utils.Common;
import com.ijp.app.drinkshopserver.Utils.ProgressRequestBody;
import com.ijp.app.drinkshopserver.Utils.UploadCallBack;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCategory extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1111;
    ImageView imgBrowser;
    EditText edtName;
    Button btnUpdate,btnDelete;

    IDrinkShopAPI mService;

    CompositeDisposable compositeDisposable;

    Uri selectedUri=null;
    String uploadedImgPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        btnDelete=findViewById(R.id.btn_delete);
        btnUpdate=findViewById(R.id.btn_update);
        edtName=findViewById(R.id.edt_name);
        imgBrowser=findViewById(R.id.img_browser);

        mService= Common.getAPI();

        compositeDisposable=new CompositeDisposable();

        displayData();

        imgBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a File"),
                        PICK_FILE_REQUEST);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();
            }
        });

    }

    private void deleteCategory() {
        compositeDisposable.add(mService.deleteCategory(Common.currentCategory.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(UpdateCategory.this, s, Toast.LENGTH_SHORT).show();
                        uploadedImgPath="";
                        selectedUri=null;

                        Common.currentCategory=null;

                        finish();
                    }
                }));
    }

    private void updateCategory() {
        if(!edtName.getText().toString().isEmpty())
        {
            compositeDisposable.add(mService.updateCategory(Common.currentCategory.getId(),
                    edtName.getText().toString(),
                    uploadedImgPath).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    Toast.makeText(UpdateCategory.this, s, Toast.LENGTH_SHORT).show();
                    uploadedImgPath="";
                    selectedUri=null;

                    Common.currentCategory=null;

                    finish();
                }
            }));
        }
    }

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
                    mService.uploadCategoryFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    uploadedImgPath=new StringBuilder(Common.BASE_URL)
                                            .append("DrinkShopServer/Category/category_image/")
                                            .append(response.body())
                                            .toString();
                                    Log.d("IMGPath",uploadedImgPath);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(UpdateCategory.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }


    private void displayData() {
        if(Common.currentCategory!=null)
        {
            Picasso.with(this)
                    .load(Common.currentCategory.getLink())
                    .into(imgBrowser);

            edtName.setText(Common.currentCategory.getName());

            uploadedImgPath=Common.currentCategory.getLink();
        }
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
