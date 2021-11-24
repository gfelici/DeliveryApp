package com.delivery.deliveryapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.delivery.deliveryapp.Firebase.DbManager;

public class SettingsActivity extends Activity {

    private DbManager dbManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.dbManager = new DbManager();
        dbManager.getUserData(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.dbManager = new DbManager();
        dbManager.getUserData(this);
        Button save = (Button) findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txtName, txtAddress, txtCity;
                txtName = findViewById(R.id.editTextName);
                txtAddress = findViewById(R.id.editTextAddress);
                txtCity = findViewById(R.id.editTextCity);

                String name = txtName.getText().toString();
                String address = txtAddress.getText().toString();
                String city = txtCity.getText().toString();

                if (name.length() == 0 || address.length() == 0 || city.length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Campi vuoti", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbManager.updateUserData(getApplicationContext(), name, address, city);
                Toast.makeText(getApplicationContext(), "Informazioni salvate", Toast.LENGTH_SHORT);
                finish();
            }
        });
    }
}
