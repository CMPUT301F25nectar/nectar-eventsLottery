package com.example.beethere.ui.myEvents;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beethere.R;
import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class InvitedAdapter extends ArrayAdapter<String> {

    private Map<String, Boolean> invitedList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InvitedAdapter(Context context, Map<String, Boolean> invitedList) {
        super(context, 0, new ArrayList<>(invitedList.keySet()));
        this.invitedList = invitedList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_invited, parent, false);
        } else {
            view = convertView;
        }

        String userID = getItem(position);
        Boolean invited = invitedList.get(userID);

        TextView name = view.findViewById(R.id.name);
        TextView inviteStatus = view.findViewById(R.id.inviteStatus);

        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(document -> {
                    User user = document.toObject(User.class);
                    name.setText(user.getName());
                });


        if (Boolean.TRUE.equals(invited)) {
            inviteStatus.setText("Declined");
            inviteStatus.setSelected(true);
        } else {
            inviteStatus.setText("Awaiting");
            inviteStatus.setSelected(false);
        }

        return view;
    }


    public void update(Map<String, Boolean> newList) {
        invitedList.clear();
        invitedList.putAll(newList);

        clear();
        addAll(new ArrayList<>(newList.keySet()));
        notifyDataSetChanged();
    }
}
