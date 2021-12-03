package com.example.smts.salesman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smts.AdminLoginActivity;
import com.example.smts.OrderHistory.SalesmanPlaceOrderActivity;
import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginSalesman extends AppCompatActivity {

    EditText etusername, etpassword;
    Button btn_salesman_login;
    int i;
    Button btndistri, btnsales;
    Context context;
    String TAG = "TAG";
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_salesman);

        context =LoginSalesman.this;
        etpassword = findViewById(R.id.etpassword);
        etusername = findViewById(R.id.et_usernameCNIC);

        btndistri = findViewById(R.id.btn_distributor_id);
        btnsales = findViewById(R.id.btn_salesman_id);
        btn_salesman_login = findViewById(R.id.btn_salesman_login_id);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("SalesmanTable");

        inItclicks();
    }

    public void inItclicks() {


      btn_salesman_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (UserTypeSelection_Activity.isNetworkAvailable(LoginSalesman.this)) {
                        salesmanLoginService();
                        Toast.makeText(LoginSalesman.this, "login", Toast.LENGTH_SHORT).show();
                    } else {
                        UserTypeSelection_Activity.openWifISettings(LoginSalesman.this);
                    }
                }
            });

        btndistri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i == 2) {
                    btnsales.setBackgroundResource(R.drawable.rect_outlinegrey);
                    btnsales.setTextColor(Color.parseColor("#aaaaaa"));
                }
                btndistri.setBackgroundResource(R.drawable.rect_outlineblue);
                btndistri.setTextColor(Color.parseColor("#009688"));

                Toast.makeText(LoginSalesman.this, "login", Toast.LENGTH_SHORT).show();

                i = 1;
                finish();
                Intent D = new Intent(LoginSalesman.this, AdminLoginActivity.class);
                startActivity(D);
            }
        });

        btnsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i == 1) {
                    btndistri.setBackgroundResource(R.drawable.rect_outlinegrey);
                    btndistri.setTextColor(Color.parseColor("#aaaaaa"));
                }

                btnsales.setBackgroundResource(R.drawable.rect_outlineblue);
                btnsales.setTextColor(Color.parseColor("#009688"));
                i = 2;
                finish();
                Intent S = new Intent(LoginSalesman.this, LoginSalesman.class);
                startActivity(S);
            }
        });


    }
    private void salesmanLoginService() {

        Log.d(TAG, "submit_salesman: inside method");
        final String username = etusername.getText().toString();
        String password = etpassword.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            Log.d(TAG, "submit_salesman: username" + username);
            Log.d(TAG, "submit_salesman: password" + password);

            Query query = databaseReference.orderByChild("SalesmanTable").equalTo(etusername.getText().toString());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child("SalesmanTable").orderByChild("cnic_salesman").equalTo(username);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

//                            Toast.makeText(LoginSalesman.this, "onDataChange", Toast.LENGTH_SHORT).show();
                            if (dataSnapshot.exists()) {
//                                Toast.makeText(LoginSalesman.this, "in", Toast.LENGTH_SHORT).show();

                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    ModelSalesman usersBean = snapshot.getValue(ModelSalesman.class);

                                    if (usersBean  != null) {
                                        if (usersBean.getPassword_salesman().equals(etpassword.getText().toString().trim())) {
                                            Intent intent = new Intent(context, SalesmanPlaceOrderActivity.class);
                                            Toast.makeText(LoginSalesman.this, "Successfuly Login..!", Toast.LENGTH_SHORT).show();
                                            intent.putExtra("cnic_salesman", username);
                                            startActivity(intent);
                                        } else {
                                            etpassword.requestFocus();
                                            etpassword.setError("enter correct password");
//                                            Toast.makeText(context, "Password is wrong try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }}
                            else {
                                etusername.requestFocus();
                                etusername.setError("invalid username..!");
                                Toast.makeText(context, "User not found..!", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                            Toast.makeText(LoginSalesman.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {

            if (username.isEmpty()){
                etusername.requestFocus();
                etusername.setError("enter username");
            }else if (password.isEmpty()){
                etpassword.requestFocus();
                etpassword.setError("enter password");
            }
        }

    }

}