package com.example.smts.salesman;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SalesmanRegistrationActivity extends AppCompatActivity {

    EditText et_name, et_cnic, et_mob, et_email, et_password;
    Button btn_addSalesman;
    Context context;
    Spinner spinnerArea;
    ArrayAdapter adapterArea;
    public static ArrayList<String> area_list;

    FirebaseDatabase database;
    DatabaseReference ref;
    ModelSalesman model;
    int maxid = 0;
    public static boolean isFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_registration);
        context = this;
        findIds();
        findSpinners();

        model = new ModelSalesman();

        ref = FirebaseDatabase.getInstance().getReference().child("SalesmanTable");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (int) snapshot.getChildrenCount();
                }else {
                    ////

                }

                if (isFirstTime) {
                    Toast.makeText(context, "Salesman Registered Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Salesman Registration failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clearFields() {

        et_name.getText().clear();
        et_cnic.getText().clear();
        et_email.getText().clear();
        et_mob.getText().clear();
        et_password.getText().clear();
        spinnerArea.setSelection(0);
    }


    void findIds(){

        btn_addSalesman = findViewById(R.id.btn_addSalesman_id);
        et_name = findViewById(R.id.et_name_id);
        et_cnic = findViewById(R.id.et_cnic_id);
        et_email = findViewById(R.id.et_email_id);
        et_mob = findViewById(R.id.et_mob_id);
        et_password = findViewById(R.id.et_password_id);

        btn_addSalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserTypeSelection_Activity.isNetworkAvailable(SalesmanRegistrationActivity.this)){
                    add_salesman();
                }else {
                    UserTypeSelection_Activity.openWifISettings(SalesmanRegistrationActivity.this);
                }
            }
        });

    }

    private void  findSpinners()
      {

        area_list = new ArrayList<>();
        area_list.clear();
        area_list.add("--Select Area--");
        area_list.add("Area-1");
        area_list.add("Area-2");
        area_list.add("Area-3");
        area_list.add("Area-4");
        area_list.add("Area-5");
        area_list.add("Area-6");

        spinnerArea = findViewById(R.id.spinner_Area_id);
        adapterArea = new ArrayAdapter<String>(SalesmanRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item,
                area_list);
        adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapterArea);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }
    //Add Salesman Details into database
    public void add_salesman() {

        String name = et_name.getText().toString();
        String cnic = et_cnic.getText().toString();
        String mob = et_mob.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String workingArea = spinnerArea.getSelectedItem().toString();

        if (!name.isEmpty() && cnic.length()==13 && name.length() >= 3 && mob.length() == 11 && !email.isEmpty() && UserTypeSelection_Activity.isValidEmail(email) && !password.isEmpty() && password.length() >=6 && spinnerArea.getSelectedItemPosition()!= 0) {

                model.setName_salesman(name);
                model.setCnic_salesman(cnic);
                model.setEmail_salesman(email);
                model.setMob_salesman(mob);
                model.setPassword_salesman(password);
                model.setWorkingArea(workingArea);
                isFirstTime = true;

                // firebase
                ref.child(String.valueOf(maxid+1)).setValue(model);


        } else {
             if (name.isEmpty() || name.length()<=3){
                et_cnic.requestFocus();
                et_cnic.setError("enter Salesman Name");
            } if (cnic.length()!=13){
                et_cnic.requestFocus();
                et_cnic.setError("please enter valid CNIC NO");
            } else if (mob.length()!=11){
                et_mob.requestFocus();
                et_mob.setError("please enter valid Mobile Number");
            } else if (password.isEmpty() || password.length() < 6) {
                et_password.setError("enter at least 6 digits/characters new password");
                et_password.requestFocus();
             }else if (!UserTypeSelection_Activity.isValidEmail(email)) {
                et_email.requestFocus();
                et_email.setError("please enter valid Email");
            } else if (spinnerArea.getSelectedItemPosition()== 0){
                spinnerArea.performClick();
                Toast.makeText(context, "please select Salesman Working Area", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "please enter valid information", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
