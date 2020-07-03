package com.marand.myplaces.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import com.marand.myplaces.model.Place;
import com.marand.myplaces.network.APIClient;
import com.marand.myplaces.util.Resource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MyRepository {
    private static final String TAG = MyRepository.class.getSimpleName();
    private static MyRepository instance;
    private MediatorLiveData<Resource<Place>> place;

    public static MyRepository getInstance() {
        if (instance == null)
            instance = new MyRepository();

        return instance;
    }

    public LiveData<Resource<Place>> makeReactiveQuery(String clientId, String clientSecret, int version,
                                                       int limit, int offset, String ll) {
        if (place == null) {
            place = new MediatorLiveData<>();
            place.setValue(Resource.loading((Place)null));
            final LiveData<Resource<Place>> source = LiveDataReactiveStreams.fromPublisher(
                    APIClient.getAPIRequest().getPlace(clientId, clientSecret, version, limit, offset, ll).onErrorReturn(throwable -> {
                        Place place = new Place();
                        place.setId(-1);
                        return place;
                    }).map((Function<Place, Resource<Place>>) place -> {
                        if (place.getId() == -1) {
                            return Resource.error("Some error happened!", null);
                        }
                        return Resource.success(place);
                    }).subscribeOn(Schedulers.io())
            );
            place.addSource(source, listResource -> {
                place.setValue(listResource);
                place.removeSource(source);
            });
        }
        return place;
    }
}
