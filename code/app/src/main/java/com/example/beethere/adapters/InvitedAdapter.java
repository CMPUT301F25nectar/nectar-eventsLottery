package com.example.beethere.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.beethere.R;
import com.example.beethere.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class InvitedAdapter extends ArrayAdapter<String> {

    private Map<String, Boolean> invitedList;
    private User user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton deleteInvited;

    /**
     *
     * @param context context of the adapter
     * @param invitedList list of entrants who have been selected from the wait-list
     */

    public InvitedAdapter(Context context, Map<String, Boolean> invitedList) {
        super(context, 0, new ArrayList<>(invitedList.keySet()));
        this.invitedList = invitedList;
    }

    /**
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view
     */

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
                    user = document.toObject(User.class);
                    name.setText(user.getName());
                });

        deleteInvited.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userID)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        showSnackbar(view, user.getName() +
                                " was removed from invites list");
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        showSnackbar(view, "Error: " + user.getName() +
                                " could not be removed from invites list");
                    });
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

    /**
     *
     * @param newList update the invited list after any changes made
     */
    public void update(Map<String, Boolean> newList) {
        invitedList.clear();
        invitedList.putAll(newList);

        clear();
        addAll(new ArrayList<>(newList.keySet()));
        notifyDataSetChanged();
    }

    public void showSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view,text, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getContext().getColor(R.color.dark_brown))
                .setTextColor(getContext().getColor(R.color.yellow));
        View snackbarView = snackbar.getView();
        TextView snackbarText = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbarText.setTextSize(20);
        snackbarText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_semibold));
        snackbar.show();
    }

}
