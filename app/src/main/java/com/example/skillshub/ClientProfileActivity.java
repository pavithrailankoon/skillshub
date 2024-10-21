package com.example.skillshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.skillshub.model.CustomDialog;

import org.w3c.dom.Text;


public class ClientProfileActivity extends ComponentActivity implements CustomDialog.CustomDialogInterface{

    ImageView backBtn;
    Button logOut,editDetails,editPassowrd;
    TextView newName,newPhoneNumber,newAddressLine1,newAddressLine2;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private Uri imageUri;
    private Uri DEFAULT_IMAGE_URI;

    ChangePassword changePassword = new ChangePassword();

    public void openDialog(View view){
        CustomDialog customDialog = new CustomDialog();
        customDialog .show(getSupportFragmentManager(),"Test Customdialog");

    }

    private FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        logOut = findViewById(R.id.logOut);
        editDetails = findViewById(R.id.editDetailsBtn);
        editPassowrd = findViewById(R.id.editPassword);

        newName = findViewById(R.id.name);
        newPhoneNumber = findViewById(R.id.phoneNumber);
        newAddressLine1 = findViewById(R.id.addressLine1);
        newAddressLine2 = findViewById(R.id.addressLine2);



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),clientHome.class);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });




    }


    @Override
    public void applyTexts(String name, String phoneNumber,String addressLine1,String addressLine2) {
        newName.setText(name);
        newPhoneNumber.setText(phoneNumber);
        newAddressLine1.setText(addressLine1);
        newAddressLine2.setText(addressLine2);


    }
}