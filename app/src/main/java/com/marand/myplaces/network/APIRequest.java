package com.marand.myplaces.network;

import com.marand.myplaces.model.Place;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIRequest {
    // If every value types were same and also unique, it would be better to replace @Query with @QueryMap.
    @GET("explore")
    Flowable<Place> getPlace(@Query("client_id") String client_id,
                             @Query("client_secret") String client_secret,
                             @Query("v") int v,
                             @Query("limit") int limit,
                             @Query("offset") int offset,
                             @Query("ll") String ll);
}
