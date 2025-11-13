
package com.example.beethere.eventclasses;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.beethere.DeviceId;
//import com.example.beethere.Event;
import com.example.beethere.ui.profile.ProfileDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetails  {
//    public static void profileecheck (@NonNull Context context,
//                                       @NonNull FragmentManager fragmentmanager,
//                                       @NonNull Runnable action){
//        String deviceid = DeviceId.get(context);
//        FirebaseFirestore.getInstance().collection("users").document(deviceid)
//                .get()
//                .addOnSuccessListener((DocumentSnapshot snapshot)->{
//                    if (snapshot.exists()){
//                        action.run();//profle exists and continue
//                    }
//                    else{
//                        ProfileDialogFragment.show(fragmentmanager, action::run);//show create profile
//                    }
//                })
//                .addOnFailureListener(fail->
//                        ProfileDialogFragment.show(fragmentmanager, action::run)
//                );
//    }
}