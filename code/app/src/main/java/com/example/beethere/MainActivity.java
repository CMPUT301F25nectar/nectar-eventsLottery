package com.example.beethere;

import android.os.Bundle;
import android.util.Log;

import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.device.DeviceId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.beethere.databinding.ActivityMainBinding;
import com.google.firebase.messaging.FirebaseMessaging;
import com.example.beethere.DatabaseFunctions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DeviceIDViewModel deviceID;
    private DatabaseFunctions dbFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceID = new ViewModelProvider(this).get(DeviceIDViewModel.class);
        deviceID.setDeviceID(DeviceId.get(this));
        dbFunctions = new DatabaseFunctions();
        getFCMToken();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Set up NavigationUI for the BottomNavigationView
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()){
                            Log.w("fcm", "Fetching FCM token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("fcm","FCM token:" + token);
                        Log.d("fcm", "Device Id:" + deviceID.getDeviceID());
                        dbFunctions.saveFCMToken(deviceID.getDeviceID(), token);
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController();
        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null) ||
                super.onSupportNavigateUp();
    }

}