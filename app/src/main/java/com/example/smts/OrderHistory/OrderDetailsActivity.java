package com.example.smts.OrderHistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import static com.example.smts.salesman.SalesmanRegistrationActivity.area_list;
import static com.example.smts.salesman.SalesmanRegistrationActivity.isFirstTime;

public class OrderDetailsActivity extends AppCompatActivity {

    String  product_name="", mob="" , date="", time="", latLong = "", order="", quantity="", salesman_id="",
            salesmanName = "", customer_id="", customerName="", workingArea="";
    EditText   et_mob, et_price, et_DateTime, et_order, et_Quantity, et_Salesman, et_SalesmanName, et_Customer, et_CustomerName;
    Button btn_updateProduct;
    Spinner spinnerArea, spinnerProduct ;
    ArrayAdapter adapterArea;
    ArrayAdapter adapterProduct;
    FirebaseDatabase database;
    ImageButton btnget_location;
    DatabaseReference ref;
    ModelOrder model;
    int maxid = 0;
    public static ArrayList<String> area_list;
    public static ArrayList<String> product_list;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

       finIds();
       findSpinners();

       Bundle extras = getIntent().getExtras();
        if (extras != null) {
            product_name = extras.getString("product");
            date = extras.getString("currentDate");
            time = extras.getString("currentTime");
            order = extras.getString("orderId");
            mob = extras.getString("mob");
            latLong = extras.getString("latLong");
            quantity = extras.getString("quantity");
            salesman_id = extras.getString("salesmanId");
            salesmanName = extras.getString("salesmanName");
            customer_id = extras.getString("customerId");
            customerName = extras.getString("customerName");
            workingArea = extras.getString("workingArea");

            //////setData
            et_DateTime.setText(date+"   /   "+time);
            et_order.setText(order);
            et_mob.setText(mob);
            et_Quantity.setText(quantity);
            et_Salesman.setText(salesman_id);
            et_SalesmanName.setText(salesmanName);
            et_Customer.setText(customer_id);
            et_CustomerName.setText(customerName);
            spinnerProduct.setSelection(product_list.indexOf(product_name)); spinnerProduct.setEnabled(false);
            spinnerArea.setSelection(area_list.indexOf(workingArea)); spinnerArea.setEnabled(false);

        }

    }

    private void finIds() {
        et_DateTime =  findViewById(R.id.et_DateTime_id);
        et_order =  findViewById(R.id.et_order_id);
        et_Quantity =  findViewById(R.id.et_Quantity_id);
        et_Salesman =  findViewById(R.id.et_Salesman_id);
        et_SalesmanName =  findViewById(R.id.et_SalesmanName_id);
        et_Customer =  findViewById(R.id.et_Customer_id);
        et_CustomerName =  findViewById(R.id.et_CustomerName_id);
        et_mob =  findViewById(R.id.et_mob_id);
        et_price =  findViewById(R.id.et_price_id);
        btnget_location =  findViewById(R.id.btnget_location_id);

        btnget_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("geo:0,0?q="+latLong);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }
    private void  findSpinners()
    {

        product_list = new ArrayList<>();
        product_list = new ArrayList<>();
        product_list.clear();
        product_list.add("--Select Product--");
        product_list.add("Product-1");
        product_list.add("Product-2");
        product_list.add("Product-3");
        product_list.add("Product-4");
        product_list.add("Product-5");
        product_list.add("Product-6");

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
        adapterArea = new ArrayAdapter<String>(OrderDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item,
                area_list);
        adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapterArea);


        spinnerProduct = findViewById(R.id.spinner_Product_id);
        adapterProduct = new ArrayAdapter<String>(OrderDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item,
                product_list);
        adapterProduct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(adapterProduct);



    }



}

