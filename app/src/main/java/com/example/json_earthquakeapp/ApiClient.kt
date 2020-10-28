package com.example.json_earthquakeapp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object ApiClient {

    private const val BASE_URL =
        "https://earthquake.usgs.gov/fdsnws/event/1/"

    val getClient: ApiServices
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiServices::class.java)
        }
}

interface ApiServices {
    @GET("query?starttime=2016-02-01&endtime=2016-02-011&format=geojson&limit=8")
    fun getEarthQuake(
        @Query("orderby") orderby: String?,
        @Query("minmagnitude") minmagnitude: String?
    ): Call<Result>
}
