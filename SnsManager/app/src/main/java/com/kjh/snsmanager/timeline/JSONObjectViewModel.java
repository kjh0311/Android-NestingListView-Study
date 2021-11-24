package com.kjh.snsmanager.timeline;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;


// 참고 사이트: https://developer.android.com/guide/fragments/communicate?hl=ko
public class JSONObjectViewModel extends ViewModel {
    private final MutableLiveData<JSONObject> object = new MutableLiveData<JSONObject>();
    public void setJsonObject(JSONObject jsonObject) {
        object.setValue(jsonObject);
    }
    public LiveData<JSONObject> getObject() {
        return object;
    }
}
