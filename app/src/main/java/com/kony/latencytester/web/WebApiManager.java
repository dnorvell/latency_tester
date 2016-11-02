package com.kony.latencytester.web;

import android.content.Context;
import android.content.SharedPreferences;

import com.kony.latencytester.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiManager {

    private static WebApi sWebApi;
    private static Context sContext;

    public static WebApi getInstance(Context _context) {
        if (sWebApi == null) {

            // Configure logging interceptor for grabbing timestamps
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client;
//            SharedPreferences prefs = _context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
//
//            // Add the interceptor to our client
//            if(prefs.getBoolean(Constants.ENABLE_TIMEOUT, false)) {
//                client = new OkHttpClient.Builder()
//                        .readTimeout(prefs.getInt(Constants.API_TIMEOUT, 6), TimeUnit.SECONDS)
//                        .connectTimeout(prefs.getInt(Constants.API_TIMEOUT, 6), TimeUnit.SECONDS)
//                        .addInterceptor(httpLoggingInterceptor)
//                        .build();
//            }
//            else {
                client = new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor)
                        .build();
//            }

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
