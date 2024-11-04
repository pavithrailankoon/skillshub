package com.example.skillshub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshub.R;
import com.example.skillshub.model.Worker;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WorkerListAdapter extends ArrayAdapter<Worker> {

    private final Context mContext;
    private final List<Worker> workerList;

    public WorkerListAdapter(Context context, List<Worker> list) {
        super(context, 0, list);
        mContext = context;
        workerList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the custom layout if needed
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_list_item_layout2, parent, false);
        }

        // Verify the position is within bounds and get the worker object
        if (position >= 0 && position < workerList.size()) {
            Worker worker = workerList.get(position);

            if (worker != null) {
                // Find views in the layout and set the data
                TextView workerNameTextView = convertView.findViewById(R.id.workerName);
                TextView districtTextView = convertView.findViewById(R.id.district);
                TextView cityTextView = convertView.findViewById(R.id.city);
                ImageView profileImageView = convertView.findViewById(R.id.mainCategoryImage);

                workerNameTextView.setText(worker.getName() != null ? worker.getName() : "N/A");
                districtTextView.setText(worker.getDistrict() != null ? worker.getDistrict() : "N/A");
                cityTextView.setText(worker.getCity() != null ? worker.getCity() : "N/A");

                // Load profile image using Picasso with a placeholder and error handling
                if (worker.getProfileUrl() != null && !worker.getProfileUrl().isEmpty()) {
                    Picasso.get()
                            .load(worker.getProfileUrl())
                            .placeholder(R.drawable.avatar) // Replace with actual placeholder
                            .error(R.drawable.avatar) // Replace with actual error image
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.avatar); // Set placeholder if no URL
                }
            }
        }

        return convertView;
    }
}
