package com.example.skillshub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class listAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private final String[] mainCategory;
    private final int[] imageArray;


    public listAdapter(Activity context, String[] mainCategory, int[] imageArray) {
        super(context,R.layout.custom_list_item_layout,mainCategory);
        this.context = context;
        this.mainCategory = mainCategory;
        this.imageArray = imageArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list_item_layout,null,true);

        TextView mainCategoryText =  rowView.findViewById(R.id.mainCategoryName);
        ImageView mainCategoryImage = rowView.findViewById(R.id.listImage);

        mainCategoryText.setText(mainCategory[position]);
        mainCategoryImage.setImageResource(imageArray[position]);

        return rowView;
    }
}

