package com.marand.myplaces.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private static final String TAG = MyViewModelFactory.class.getSimpleName();
    private Application mApplication;
    private String mClientId, mClientSecret, mLl;
    private int mVersion, mLimit, mOffset;

    public MyViewModelFactory(Application application, String clientId, String clientSecret, int version,
                              int limit, int offset, String ll) {

        this.mApplication = application;
        this.mClientId = clientId;
        this.mClientSecret = clientSecret;
        this.mVersion = version;
        this.mLimit = limit;
        this.mOffset = offset;
        this.mLl = ll;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyViewModel(mApplication, mClientId, mClientSecret, mVersion, mLimit, mOffset, mLl);
    }

}
