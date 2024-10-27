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

    private Context mContext;
    private List<Worker> workerList;

    public WorkerListAdapter(Context context, List<Worker> list) {
        super(context, 0, list);
        mContext = context;
        workerList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the custom layout
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_list_item_layout2, parent, false);
        }

        // Get the worker for the current position
        Worker worker = workerList.get(position);

        // Find views in the layout and set the data
        TextView workerNameTextView = convertView.findViewById(R.id.workerName);
        TextView districtTextView = convertView.findViewById(R.id.district);
        TextView cityTextView = convertView.findViewById(R.id.city);
        ImageView profileImageView = convertView.findViewById(R.id.mainCategoryImage);

        workerNameTextView.setText(worker.getName());
        districtTextView.setText(worker.getDistrict());
        cityTextView.setText(worker.getCity());

        // Load profile image
        if (worker.getProfileUrl() != null && !worker.getProfileUrl().isEmpty()) {
            String imageUrl = worker.getProfileUrl();
            Picasso.get().load(imageUrl).into(profileImageView);
        }

        return convertView;
    }
}

