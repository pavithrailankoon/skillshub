package com.example.skillshub;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class listAdapter extends ArrayAdapter<String> {


    private final Activity context;

    private final String[] mainCategory;
    private final int[] mainCategoryIcon;


    public listAdapter(Activity context, String[] mainCategory, int[] mainCategoryIcon) {
        super(context,R.layout.custom_list_item_layout,mainCategory);
        this.context = context;
        this.mainCategory = mainCategory;
        this.mainCategoryIcon = mainCategoryIcon;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list_item_layout,null,true);

        TextView mainCategoryText = rowView.findViewById(R.id.mainCategoryName_textView);
        ImageView mainCategoryImage = rowView.findViewById(R.id.mainCategoryName_textView);

        mainCategoryText.setText(mainCategory[position]);
        mainCategoryImage.setImageResource(mainCategoryIcon[position]);

        return rowView;

    }
}
