/**
 * Helper to get a device id as a unique identifier using ANDROID_ID
 */
package com.example.beethere;

import android.content.Context;
import android.provider.Settings;

public class DeviceId {
    public static String get(Context context){
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return (id==null)? "unknown":id;
    }
}
