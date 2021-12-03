package com.example.smts.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class AddProductActivity extends AppCompatActivity {

    String product_name = "",product_id = "", product_price = "";
    EditText  etProduct_name,  et_Product_id, et_productPrice;
    Button btn_addProduct;
    Context context;
    Spinner spinnerArea;
    ArrayAdapter adapterArea;
    public static ArrayList<String> area_list;

    FirebaseDatabase database;
    DatabaseReference ref;
    ModelProducts model;
    int maxid = 0;
    public static boolean isFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);
        context = this;
        findIds();

        model = new ModelProducts();
        ref = FirebaseDatabase.getInstance().getReference().child("ProductsTable");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (int) snapshot.getChildrenCount();
                }else {
                    ////

                }

                if (isFirstTime) {
                    Toast.makeText(context, "Product Registered Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Product Registration failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        Intent i2 = new Intent(AddProductActivity.this, Products_List_Activity.class);
        startActivity(i2);
    }

    private void clearFields() {

        etProduct_name.getText().clear();
        et_Product_id.getText().clear();
        et_productPrice.getText().clear();

    }


    void findIds(){

        btn_addProduct = findViewById(R.id.btn_save_Product_id);
        etProduct_name = findViewById(R.id.et_nameProduct_id);
        et_productPrice = findViewById(R.id.et_price_id);
        et_Product_id = findViewById(R.id.et_Product_id);

        btn_addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserTypeSelection_Activity.isNetworkAvailable(AddProductActivity.this)){
                    add_product();
                }else {
                    UserTypeSelection_Activity.openWifISettings(AddProductActivity.this);
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }
    //Add product Details into database
    public void add_product() {

        String name = etProduct_name.getText().toString();
        String product_id = et_Product_id.getText().toString();
        String price = et_productPrice.getText().toString();


        if (!name.isEmpty() && !product_id.isEmpty() && !price.isEmpty()) {

                model.setProduct_name(name);
                model.setProduct_id(product_id);
                model.setProduct_price(price);

                isFirstTime = true;

                // firebase
                ref.child(String.valueOf(maxid+1)).setValue(model);


        } else {
            if (name.isEmpty()){
                etProduct_name.requestFocus();
                etProduct_name.setError("enter Product Name..!");
            } if (product_id.isEmpty()){
                et_Product_id.requestFocus();
                et_Product_id.setError("please enter Product ID..!");
            } else if (price.length()!=11){
                et_productPrice.requestFocus();
                et_productPrice.setError("please enter Product Price..!");
            }
        }
    }
}
