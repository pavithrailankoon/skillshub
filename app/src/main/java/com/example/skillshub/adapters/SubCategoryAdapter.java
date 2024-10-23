package com.example.skillshub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.skillshub.R;

import java.util.ArrayList;

public class SubCategoryAdapter  extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> subCategoryList;

    // Constructor to initialize the adapter
    public SubCategoryAdapter(Context context, ArrayList<String> categories) {
        super(context, 0, categories);
        this.mContext = context;
        this.subCategoryList = subCategoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_list_item_layout, parent, false);
        }

        // Get the category name at this position
        String categoryName = getItem(position);

        // Find the TextView in the custom_list_item_layout.xml layout
        TextView categoryNameTextView = convertView.findViewById(R.id.mainCategoryName);

        // Set the category name to the TextView
        categoryNameTextView.setText(categoryName);

        return convertView;
    }
}
