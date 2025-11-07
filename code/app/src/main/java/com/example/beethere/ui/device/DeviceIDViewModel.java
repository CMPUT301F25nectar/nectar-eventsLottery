package com.example.beethere.ui.device;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.beethere.DeviceId;

public class DeviceIDViewModel extends ViewModel {
    private String deviceID;

    public DeviceIDViewModel(Context context){
        this.deviceID = "";
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
