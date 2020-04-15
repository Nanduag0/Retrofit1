package com.example.retrofit1;


import org.w3c.dom.Comment;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface  JsonPlaceHolderApi
{


    @Multipart
    @POST("/upload")
    Call<ResponseBody> postPhoto
            (
                    @Part MultipartBody.Part photo,
                    @Part("upload") RequestBody description);

   /* @FormUrlEncoded
    @POST("uploads")
    Call<ImageClass> uploadImage(@Field("title") String title,@Field("image")String image);
  */
/*
    @GET("posts")
    Call<List<Post>> getPost(@Query("userId") Integer[] userId,
                             @Query("_sort") String sort,
                             @Query("_order") String order);

    @GET("posts")
    Call<List<Post>> getPost(@QueryMap Map<String, String> parameters);


    /*@GET("post/{id}/comments")
    Call<List<Comments>> getComments(@Path("id") int posId);

    @GET
    Call<List<Comment>> getComments(@Url String url);

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @FormUrlEncoded
    @POST
    Call<Post> createPost(@Field("userId") int  userId,
                          @Field("title") String title,
                          @Field("body") String text);
    @FormUrlEncoded
    @POST("posts")
    Call<Post> createPost(@FieldMap Map<String,String> fields);

    @Headers({"Static-Header: 123","Static-Header2:456"})
    @PUT("posts/{id}")
    Call<Post> putPost(@Header("Dynamic-Header") String header,
                       @Path("id") int id,
                       @Body Post post);

    @PATCH("posts/{id}")
    Call<Post> patchPost(@HeaderMap Map<String,String> headers,
                         @Path("id") int id,
                         @Body Post post);


    @PUT("posts/{id}")
    Call<Post> putPost(@Path("id") int id,@Body Post post);

    @PUT("posts/{id}")
    Call<Post> patchPost(@Path("id")int id,@Body Post post);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id")int id);*/

}
