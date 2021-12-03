package com.example.smts.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.smts.salesman.SalesmanRegistrationActivity.isFirstTime;

public class ProductDetailsActivity extends AppCompatActivity {

    String key_id = "",product_name = "",product_id = "", product_price = "";
    EditText  etProduct_name,  et_Product_id, et_productPrice;
    Button btn_updateProduct;

    FirebaseDatabase database;
    DatabaseReference ref;
    ModelProducts model;
    int maxid = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        etProduct_name =  findViewById(R.id.et_nameProduct_id);
        et_Product_id =  findViewById(R.id.et_Product_id);
        et_productPrice =  findViewById(R.id.et_price_id);

        btn_updateProduct =  findViewById(R.id.btn_updateProduct_id);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key_id = extras.getString("key_id");
            product_name = extras.getString("product_name");
            product_id = extras.getString("product_id");
            product_price = extras.getString("product_price");


        }

        etProduct_name.setText("" + product_name);
        et_Product_id.setText("" + product_id);
        et_productPrice.setText("" + product_price);


        model = new ModelProducts();
        ref = FirebaseDatabase.getInstance().getReference().child("ProductsTable");

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    maxid = (int) snapshot.getChildrenCount();
//                }else {
//                    ////
//                }
//                if (isFirstTime) {
//                    Toast.makeText(ProductDetailsActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ProductDetailsActivity.this, "Product Update failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


        btn_updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserTypeSelection_Activity.isNetworkAvailable(ProductDetailsActivity.this)) {
                  updateProductInfo();
                } else {
                  UserTypeSelection_Activity.openWifISettings(ProductDetailsActivity.this);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }

    public void updateProductInfo() {

        String name = etProduct_name.getText().toString();
        String product_id = et_Product_id.getText().toString();
        String price = et_productPrice.getText().toString();


        if (!name.isEmpty() && !product_id.isEmpty() && !price.isEmpty()) {

            model.setProduct_name(name);
            model.setProduct_id(product_id);
            model.setProduct_price(price);

            isFirstTime = true;

            // firebase

//            ref.child(String.valueOf(maxid)).setValue(model);

            Map<String, Object> map = new HashMap<>();
            map.put("product_id", product_id);
            map.put("product_name", name);
            map.put("product_price", price);
    ;

            FirebaseDatabase.getInstance().getReference("ProductsTable").child(key_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(ProductDetailsActivity.this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (!task.isSuccessful()){
                        Toast.makeText(ProductDetailsActivity.this, "Product Not Updated..!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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

