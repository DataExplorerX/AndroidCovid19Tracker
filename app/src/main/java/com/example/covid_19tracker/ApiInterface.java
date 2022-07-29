package com.example.covid_19tracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    String Base_URL="https://corona.lmao.ninja/v2/";

    @GET("countries")
    Call<List<ModelClass>> getcountrydata();


}
