package com.garikalaverdyan.contacts.data;

import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ContentAPI {
    @POST("/addContact")
    Call<Contact> addContact(@Body Contact contact);

    @GET("/getContactList")
    Call<HashMap<String, Contact>> getContactList();

    @PUT("/changeContact")
    Call<Contact> changeContact(@Body Contact contact);

    @DELETE("/deleteContact")
    Call<String> deleteContact(@Query("key") String key);

    @GET("/getFavoriteContacts")
    Call<ArrayList<Contact>> getFavoriteContact();

    @POST("/changeFavorite")
    Call<Contact> changeValue(@Query("key") String key, @Query("favorite") boolean favorite);
}