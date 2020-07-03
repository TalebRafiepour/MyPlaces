package com.marand.myplaces.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.marand.myplaces.model.Place;
import com.marand.myplaces.repository.MyRepository;
import com.marand.myplaces.util.Resource;

public class MyViewModel extends AndroidViewModel {
    private static final String TAG = MyViewModel.class.getSimpleName();
    private LiveData<Resource<Place>> place;

    public MyViewModel(@NonNull Application application, String clientId, String clientSecret, int version,
                       int limit, int offset, String ll) {

        super(application);
        MyRepository myRepository = MyRepository.getInstance();
        place = myRepository.makeReactiveQuery(clientId, clientSecret, version, limit, offset, ll);
    }

    public LiveData<Resource<Place>> getPlace() {
        return place;
    }

}
