package com.kony.latencytester.web;

import com.kony.latencytester.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiManager {

    private static WebApi sWebApi;

    public static WebApi getInstance() {
        if (sWebApi == null) {

            // Configure logging interceptor for grabbing timestamps
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Add the interceptor to our client
            OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor)
                        .build();

            // Configure and setup retrotfit and point it to our client
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            sWebApi = retrofit.create(WebApi.class);
        }
        return sWebApi;
    }

}
