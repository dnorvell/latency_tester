package com.kony.latencytester.web;


import com.kony.latencytester.entities.Contact;
import com.kony.latencytester.entities.CustomLogicApiResponse;
import com.kony.latencytester.entities.SimpleResponse;
import com.kony.latencytester.entities.SyncUploadResponse;
import com.kony.latencytester.entities.UpdateContactPayload;
import com.kony.latencytester.entities.UpdateResponse;

import java.io.File;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;


public interface WebApi {

    @GET("mailapp/api/v1/contact")
    Call<SimpleResponse> getSimpleApi();

    @POST("Stubs/small")
    Call<CustomLogicApiResponse> postCustomLogicApi();

    @GET("data/v1/Customers/objects/user?%24filter=email%20eq%20'kellyholcomb@quordate.com")
    Call<Contact> getContact();

    @PUT("data/v1/Customers/objects/user")
    Call<UpdateResponse> updateConsumer(@Body UpdateContactPayload payload);

    @GET("data/v1/Customers/objects/user")
    Call<Contact> fullOfflineSync();

    @GET("data/v1/Customers/objects/user?%24top=100")
    Call<Contact> partialOfflineSync();

    @Multipart
    @PUT("data/v1/Customers/objects/user")
    Call<SyncUploadResponse> syncUpload(@Part("file") RequestBody _file);

}
