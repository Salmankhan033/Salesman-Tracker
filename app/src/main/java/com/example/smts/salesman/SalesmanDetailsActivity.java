package com.example.smts.salesman;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.smts.salesman.SalesmanRegistrationActivity.area_list;
import static com.example.smts.salesman.SalesmanRegistrationActivity.isFirstTime;

public class SalesmanDetailsActivity extends AppCompatActivity {

    String key_id = "",sale_name = "",sale_cnic = "", mobile = "", email = "", password = "", workingArea = "";
    EditText  etsale_name,  et_sale_cnic, et_mob, et_email, et_password;
    Spinner spinnerArea;
    ArrayAdapter adapterArea;
    Button btn_updateSalesman;

    FirebaseDatabase database;
    DatabaseReference ref;
    ModelSalesman model;
    int maxid = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_details);
        etsale_name =  findViewById(R.id.etsale_name_id);
        et_email =  findViewById(R.id.et_email_id);
        et_mob =  findViewById(R.id.et_mob_id);
        et_sale_cnic =  findViewById(R.id.et_cnic_id);
        et_password =  findViewById(R.id.et_password_id);
        btn_updateSalesman =  findViewById(R.id.btn_updateSalesman_id);
        findSpinners();
//        et_id_salesman =  findViewById(R.id.id_salesman);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key_id = extras.getString("key_id");
            sale_name = extras.getString("name_salesman");
            sale_cnic = extras.getString("cnic_salesman");
            mobile = extras.getString("mob_salesman");
            email = extras.getString("email_salesman");
            workingArea = extras.getString("workingArea");
            password = extras.getString("password_salesman");

        }

        etsale_name.setText("" + sale_name);
        et_email.setText("" + email);
        et_sale_cnic.setText("" + sale_cnic);
        et_mob.setText("" + mobile);
        spinnerArea.setSelection(area_list.indexOf(workingArea));
        et_password.setText("" + password);

        model = new ModelSalesman();
        ref = FirebaseDatabase.getInstance().getReference().child("SalesmanTable");

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    maxid = (int) snapshot.getChildrenCount();
//                }else {
//                    ////
//                }
//                if (isFirstTime) {
//                    Toast.makeText(SalesmanDetailsActivity.this, "Salesman Updated Successfully", Toast.LENGTH_SHORT).show();
////                    clearFields();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(SalesmanDetailsActivity.this, "Salesman Registration failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


        btn_updateSalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserTypeSelection_Activity.isNetworkAvailable(SalesmanDetailsActivity.this)) {
                  updateSalesmanInfo();
                } else {
                  UserTypeSelection_Activity.openWifISettings(SalesmanDetailsActivity.this);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }

    private void updateSalesmanInfo() {

            String name = etsale_name.getText().toString();
            String cnic = et_sale_cnic.getText().toString();
            String mob = et_mob.getText().toString();
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            String workingArea = spinnerArea.getSelectedItem().toString();

            if (!name.isEmpty() && cnic.length()==13 && name.length() >= 3 && mob.length() == 11  && !email.isEmpty() && UserTypeSelection_Activity.isValidEmail(email) && !password.isEmpty() && password.length() >=6 && spinnerArea.getSelectedItemPosition()!= 0) {

                    model.setName_salesman(name);
                    model.setCnic_salesman(cnic);
                    model.setEmail_salesman(email);
                    model.setMob_salesman(mob);
                    model.setPassword_salesman(password);
                    model.setWorkingArea(workingArea);

                    isFirstTime = true;

                   openUpdateDialog(this, name,  email, cnic, mob, password, workingArea);

            } else {
                if (name.isEmpty() || name.length()<=3){
                    etsale_name.requestFocus();
                    etsale_name.setError("enter Salesman Name");
                } if (cnic.length()!=13){
                    et_sale_cnic.requestFocus();
                    et_sale_cnic.setError("please enter valid CNIC NO");
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
                    Toast.makeText(SalesmanDetailsActivity.this, "please select Salesman Working Area", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "please enter valid information", Toast.LENGTH_SHORT).show();

                }
            }
    }
    public void openUpdateDialog(final Context context, final String name, final String email, final String cnic, final String mob, final String password, final String workingArea) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setCancelable(true);
        mDialog.setTitle("Delete Alert..!");
        mDialog.setMessage("Do you want to Update this Record..!");
        mDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Map<String, Object> map = new HashMap<>();
                map.put("cnic_salesman", cnic);
                map.put("email_salesman", email);
                map.put("mob_salesman", mob);
                map.put("name_salesman", name);
                map.put("password_salesman", password);
                map.put("workingArea", workingArea);

               FirebaseDatabase.getInstance().getReference("SalesmanTable").child(key_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {

                       if (task.isSuccessful()){
                           Toast.makeText(context, "Salesman Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                           finish();
                       }else if (!task.isSuccessful()){
                           Toast.makeText(context, "Salesman Profile Not Updated..!", Toast.LENGTH_SHORT).show();
                       }
                   }
               });



            }
        });

        mDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.create().show();
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
        adapterArea = new ArrayAdapter<String>(SalesmanDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item,
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

}

