package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class clientHome2 extends AppCompatActivity {

    private CircleImageView profileImageButton;
    private ImageButton filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home2);

        // client profile button code

             profileImageButton = (CircleImageView) findViewById(R.id.avatar);

              profileImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                  public void onClick(View v) {
                       Toast.makeText(clientHome2.this, "Client profile", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(clientHome2.this, ClientProfileActivity.class);
                       startActivity(intent);
                   }
               });


        filterButton = (ImageButton) findViewById(R.id.filter_button);

        filterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(clientHome2.this, "Filter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(clientHome2.this, FilterActivity.class);
                startActivity(intent);
            }

        });


        ListView listView = findViewById(R.id.listView2);

        String[] mainCategoryName = {"Electricians","CCTV","Blumbers,","Masons","Welders","Carpenters"};

        int[] mainCategoryImage = {R.drawable.technicians,R.drawable.drivers,R.drawable.drivers,R.drawable.technicians,R.drawable.technicians,R.drawable.drivers};


        listAdapter adapter = new listAdapter(this,mainCategoryName,mainCategoryImage);
        listView.setAdapter(adapter);
    }
}