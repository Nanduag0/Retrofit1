package com.example;

import android.content.Context;

import com.example.retrofit1.JsonPlaceHolderApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null)
        {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://127.0.0.1:3000/")
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

        }
        return retrofit;
        //
    }
}
