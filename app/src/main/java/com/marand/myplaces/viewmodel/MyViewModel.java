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
    private LiveData<Resource<Place>> _place;
    private MyRepository myRepository;
    public MyViewModel(@NonNull Application application) {
        super(application);
        myRepository = MyRepository.getInstance();
    }

    public LiveData<Resource<Place>> getPlace(String clientId, String clientSecret, int version, int limit, int offset, String ll) {
        _place = myRepository.makeReactiveQuery(clientId, clientSecret, version, limit, offset, ll);
        return _place;
    }

    public LiveData<Resource<Place>> place() {
        return _place;
    }


}
