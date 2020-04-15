package com.example.retrofit1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main2Activity extends AppCompatActivity {

    private Button capture_button;

    private ImageView image;
    Bitmap mBitmap;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    String pathToFile;
    private TextView textres, pictureinfo, desces;


   // public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.imageview);
        capture_button = findViewById(R.id.button_click);
        textres = findViewById(R.id.text_result);
        pictureinfo = findViewById(R.id.picture);
        desces = findViewById(R.id.description);
        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Uri outputFileUri = getCaptureImageOutputUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent,1);
                }
            }
        });
        multipartImageUpload();
    }
    private Uri getCaptureImageOutputUri()
    {
        Uri outputFileUri = null;
        String name = new SimpleDateFormat("yyyyMMdd_MMmmaa").format(new Date());
        File storagebit = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File images = null;

        try {
            images = File.createTempFile(name, ".jpg", storagebit);
        } catch (IOException e) {
            Log.d("mylog", "Excep" + e.toString());
        }

      /*  File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }*/
        if (images != null) {
            pathToFile = images.getAbsolutePath();
            Toast.makeText(Main2Activity.this, "efhbfhrh" + pathToFile, Toast.LENGTH_LONG).show();
            outputFileUri = FileProvider.getUriForFile(Main2Activity.this, "com.example.retrofit1.fileprovider", images);
            //outputFileUri=Uri.fromFile(images);

        }
        return outputFileUri;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            String filePath = getImageFilePath(data);

                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath);
                    image.setImageBitmap(mBitmap);
                }

        }

    }
    public String getImageFilePath(Intent data) {

        return getImageFromFilePath(data);
    }
    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = {
                MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void multipartImageUpload()
    {
        try {
            File filesDir = getApplicationContext().getFilesDir();
            File file = new File(filesDir,"image" + ".jpg");
            OutputStream os;
            try {
                os = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
           // mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
           // Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
            byte[] bitmapdata = bos.toByteArray();
           //   long leng=bitmapdata.length;
           // String img= Base64.encodeToString(bitmapdata,Base64.DEFAULT);

          //    Toast.makeText(Main2Activity.this,"Size"+leng,Toast.LENGTH_LONG).show();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.43.3:3000/")
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

            jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi .class);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

            Call<ResponseBody> req =jsonPlaceHolderApi.postPhoto(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code() == 200) {

                    }

                  //  Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), "Request failed" + t.toString(), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        };
    }
}
