package com.marand.myplaces.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.marand.myplaces.util.Constants.BASE_URL;

public class APIClient {
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static APIRequest apiRequest = retrofit.create(APIRequest.class);

    public static APIRequest getAPIRequest(){
        return apiRequest;
    }

}
