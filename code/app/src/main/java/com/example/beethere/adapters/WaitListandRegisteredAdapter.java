package com.example.beethere.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.example.beethere.R;
import com.example.beethere.User;

public class WaitListandRegisteredAdapter extends ArrayAdapter<User>{

    private ArrayList<User> users;

    /**
     *
     * @param context context of adapter
     * @param users users within the waitlist and registered list
     */
    public WaitListandRegisteredAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
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
        if (convertView == null){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_waitlist_and_registered, parent, false);
        } else {
            view = convertView;
        }

        User user = users.get(position);
        TextView name =  view.findViewById(R.id.name);
        name.setText(user.getName());
        return view;
    }

    /**
     *
     * @param newList update list
     */

    public void update(ArrayList<User> newList) {
        clear();
        addAll(newList);
        notifyDataSetChanged();
    }

}
