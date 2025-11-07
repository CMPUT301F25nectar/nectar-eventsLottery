package com.example.beethere.ui.device;


import androidx.lifecycle.ViewModel;

public class DeviceIDViewModel extends ViewModel {
    private String deviceID;

    public DeviceIDViewModel(){
        this.deviceID = "";
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
