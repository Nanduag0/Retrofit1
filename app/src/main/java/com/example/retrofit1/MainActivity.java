package com.example.retrofit1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.NetworkClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

import static androidx.core.os.LocaleListCompat.create;

public class MainActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    Bitmap captureImage;
    private Uri fileUri;
    private Button capture_button;

    private ImageView image;
    private TextView textres, pictureinfo, desces;
    ResponseBody responseBody;
    File images;
    String picturePath;
    Context context;
    Bitmap mbitmap;
    String pathToFile;
    Uri photoURI;
    File photoFile;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.imageview);
        capture_button = findViewById(R.id.button_click);
        textres = findViewById(R.id.text_result);
        pictureinfo = findViewById(R.id.picture);
        desces = findViewById(R.id.description);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
       /* if(Build.VERSION.SDK_INT>=23)
        {
            requestPermissions(new String[],(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),100);
        }*/
        //customisation
        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                   // Uri outputFileUri = getCaptureImageOutputUri();
                    photoFile = null;
                    photoFile = createPhotoFile();
                    if (photoFile != null) {
                        pathToFile = photoFile.getAbsolutePath();
                        Toast.makeText(MainActivity.this, "efhbfhrh" + pathToFile, Toast.LENGTH_LONG).show();
                        photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.retrofit1.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent,1);
                    }

                }
                //multiupload();
                uploadImage(pathToFile, photoURI, captureImage);
            }

        });
        // uploadImage();
        // Gson gson=new GsonBuilder().serializeNulls().create();

        // getPosts();
        //getComments();
        //  createPost();
        //updatePost();
        //deletePost();
        // uploadPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {

            captureImage = BitmapFactory.decodeFile(pathToFile);

            image.setImageBitmap(captureImage);
            Toast.makeText(MainActivity.this, "path:" + pathToFile, Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "orginal path" + photoFile, Toast.LENGTH_LONG).show();
            //  Uri tempuri=getImageUri(getApplicationContext(),captureImage);

        }
    }

    public File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_MMmmaa").format(new Date());
        File storagebit = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File images = null;

        try {
            images = File.createTempFile(name, ".jpg", storagebit);
        } catch (IOException e) {
            Log.d("mylog", "Excep" + e.toString());
        }
        return images;
    }
     private String getMimeType(String pathToFile)
     {
       String extension= MimeTypeMap.getFileExtensionFromUrl(pathToFile);
       return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
     }


       public void uploadImage(String pathToFile, Uri photoURI,Bitmap cptureImage)
      {

          File file = new File(pathToFile);
         String content_type=getMimeType(file.getPath());

         /*Toast.makeText(MainActivity.this,"SIze"+length,Toast.LENGTH_LONG).show();
          try {
              FileOutputStream os = new FileOutputStream(file);
             // cptureImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
              os.flush();
              os.close();
          } catch (Exception e) {
              Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
          }


          ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //  captureImage.compress(Bitmap.CompressFormat.JPEG, 0, bos);
          byte[] bitmapdata = bos.toByteArray();
        //  String imgdata=Base64.encodeToString(bitmapdata,Base64.DEFAULT);


          FileOutputStream fos = null;
          try {
              fos = new FileOutputStream(file);

          } catch (FileNotFoundException e) {
              e.printStackTrace();
          }
          try {
              fos.write(bitmapdata);
          } catch (IOException e) {
              e.printStackTrace();
          }
          try {
              fos.flush();
          } catch (IOException e) {
              e.printStackTrace();
          }
          try {
              fos.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
*/

          //Toast.makeText(MainActivity.this,"files!!!"+file.getAbsoluteFile(),Toast.LENGTH_LONG).show();
          Gson gson = new GsonBuilder()
                  .setLenient()
                  .create();
    //creating retrofit object
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.43.3:3000/")
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

    // jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    RequestBody fileReqBody = RequestBody.create(MediaType.parse(content_type),file);
    // Create MultipartBody.Part using file request-body,file name and part name
    MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
    //Toast.makeText(MainActivity.this,"byte"+imgdata,Toast.LENGTH_LONG).show();
    //Create request body with text description and text media type

    String desc = "This is the profile image";
    RequestBody description = RequestBody.create(desc, JSON);
    //creating our api
    // Api api = retrofit.create(Api.class);
       //  desces.setText(imgdata.toString());
    //creating a call and calling the upload image method
    // Call<MyResponse> call = api.uploadImage(requestFile, descBody);
        Toast.makeText(MainActivity .this,"**desc**"+description,Toast.LENGTH_LONG).

    show();
    //Retrofit retrofit = NetworkClient.getRetrofit(this);
    jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi .class);
    Call<ResponseBody> call = jsonPlaceHolderApi.postPhoto(part, description);
        call.enqueue(new Callback<ResponseBody>()

    {
        @Override
        public void onResponse (Call < ResponseBody > call, Response < ResponseBody > response)
        {
            Toast.makeText(MainActivity.this, "Successful Upload!!!!!!!!!!", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onFailure (Call < ResponseBody > call, Throwable t)
        {
            // if (t instanceof IOException) {
            //   Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "mkmkgmkgmkgmkg" + t.toString(), Toast.LENGTH_LONG).show();
            // logging probably not necessary
            // }
            // else {
            // Toast.makeText(MainActivity.this, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
            // todo log to some central bug tracking service
        }

    });
}
     }

        //
        /*Call<ResponseBody> call = jsonPlaceHolderApi.uploadPhoto(part, description);
        /*call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response)
            {
                Toast.makeText(MainActivity.this, "Successfully Uploaded  ********** !!!!!!!!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });*/




      /*  Bitmap bm=BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagebytes=byteArrayOutputStream.toByteArray();
        String img= Base64.encodeToString(imagebytes,Base64.DEFAULT);
        Toast.makeText(MainActivity.this,"image"+img,Toast.LENGTH_LONG);
        String tit="Laptop";
      Call<ImageClass> call=jsonPlaceHolderApi.uploadImage(tit,img);

       call.enqueue(new Callback<ImageClass>() {
           @Override
           public void onResponse(Call<ImageClass> call, Response<ImageClass> response)
           {
               ImageClass imageClass=response.body();
               Toast.makeText(MainActivity.this,"Server Response"+imageClass.getResponse(),Toast.LENGTH_LONG);
               image.setVisibility(View.GONE);
               capture_button.setEnabled(false);
               textres.setText("HATE");


           }

           @Override
           public void onFailure(Call<ImageClass> call, Throwable t) {

           }
       });*/

    /*private String imagetoString()
    {

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        captureImage.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagebytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagebytes,Base64.DEFAULT);
    }*/
    /*private void getPosts()
    {
        Map<String,String> parameters =new HashMap<>();
        parameters.put("userId","1");
        parameters.put("sort","id");
        parameters.put("order","desc");
        Call<List<Post>> call=jsonPlaceHolderApi.getPost(parameters);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful())
                {
                    textviewresult.setText("Code"+response.code());
                    return;
                }
                List<Post> posts=response.body();
                for(Post post:posts)
                {
                    String content="";
                    content +="ID :"+post.getId()+"\n";
                    content +="User Id"+post.getUserId()+"\n";
                            content +="Iitle"+post.getTitle()+"\n";
                            content+="Twxt"+post.getText()+"\n";
                            textviewresult.append(content);
                }

            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textviewresult.setText(t.getMessage());

            }
        });
    }

   /* private   void getComments()
    {

        Call<List<Comments>> call=jsonPlaceHolderApi.getComments();
        call.enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response)
            {
                if(!response.isSuccessful())
                {
                    textviewresult.setText("Code: " +response.code());
                    return;
                }
                List<Comments> comments=response.body();

                for(Comments comments1 : comments)
                {
                    String content="";
                    content+="ID"+comments1.getId()+"\n";
                    content+="Post Id"+comments1.getPostId()+"\n";
                    content+="Name:"+comments1.getName()+"\n";
                    content+="Email"+comments1.getEmail()+"\n";
                    content+="Text"+comments1.getText()+"\n\n";
                    textviewresult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Comments>> call, Throwable t) {
               textviewresult.setText(t.getMessage());
            }
        });

    }*/
   /* private void createPost()
   {
       Post post=new Post(2,"rjgnrjgn",45,"nrjgnjrgn");
       Map<String,String > fields=new HashMap<>();
       fields.put("userId","2");
       fields.put("title","New Title");
       fields.put("text","nfrgjrng");
       Call<Post> call=jsonPlaceHolderApi.createPost(fields);
       call.enqueue(new Callback<Post>() {
           @Override
           public void onResponse(Call<Post> call, Response<Post> response) {

               if(!response.isSuccessful())
               {
                   textviewresult.setText("Code: "+ response.code());
                   return;
               }
               Post post1=response.body();

               String content="";
               content+="Code:"+response.code()+"\n";
               content+="User Id "+post1.getUserId()+"\n";;
               content+="Tiitle "+post1.getTitle()+"\n";
               content+="Id "+post1.getId()+"\n";
               content+="Text"+post1.getText()+"\n";
               textviewresult.append(content);

           }

           @Override
           public void onFailure(Call<Post> call, Throwable t)
           {

             //textviewresult.setText();

           }
       });

   }*/
  /* private void updatePost() {
       Post post = new Post(12, null, 23, "New title");
       Map<String,String> headers =new HashMap<>();
       headers.put("Map-Header1","def");
       headers.put("Map-Header2","ghi");
       Call<Post> call = jsonPlaceHolderApi.patchPost(headers,5,post);
       call.enqueue(new Callback<Post>() {
           @Override
           public void onResponse(Call<Post> call, Response<Post> response)
           {
               if(!response.isSuccessful())
               {
                   textviewresult.setText("Code: "+ response.code());
                   return;
               }
               Post post1=response.body();

               String content="";
               content+="Code:"+response.code()+"\n";
               content+="User Id :"+post1.getUserId()+"\n";;
               content+="Tiitle :"+post1.getTitle()+"\n";
               content+="Id :"+post1.getId()+"\n";
               content+="Text :"+post1.getText()+"\n";
               textviewresult.append(content);


           }

           @Override
           public void onFailure(Call<Post> call, Throwable t)
           {
               //textviewresult.setText();

           }
       });

   }*/

   /*private void  deletePost()
   {
      Call<Void> call=jsonPlaceHolderApi.deletePost(5);
      call.enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response)
          {
            textviewresult.setText("Code:"+ response.code());
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t)
          {
            textviewresult.setText(t.getMessage());
          }
      });
   }*/

