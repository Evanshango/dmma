package com.brian.dmgnt.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static final String BASE_URL_ONE = "https://covid19.mathdro.id/api/";
    private static final String BASE_URL_TWO = "https://covid19.mathdro.id/";
    private static Retrofit countries = null;
    private static Retrofit general = null;
    private static OkHttpClient sOkHttpClient;

    private static Retrofit getCountries(){
        if (countries == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            showLogs(logging);

            countries = new Retrofit.Builder()
                    .baseUrl(BASE_URL_ONE)
                    .client(sOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return countries;
    }

    private static Retrofit getGeneral(){
        if (general == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            showLogs(logging);

            general = new Retrofit.Builder()
                    .baseUrl(BASE_URL_TWO)
                    .client(sOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return general;
    }

    private static void showLogs(HttpLoggingInterceptor logging) {
        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    public static Api getApiClient() {
        return getGeneral().create(Api.class);
    }

    public static Api getCountriesApiClient() {
        return getCountries().create(Api.class);
    }
}
