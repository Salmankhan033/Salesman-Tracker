package com.example.smts.product;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smts.OrderHistory.Orders_list_activity;
import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.example.smts.design.MBottomBar;
import com.example.smts.salesman.Salesman;
import com.example.smts.OrderHistory.SalesmanPlaceOrderActivity;
import com.example.smts.salesman.SalesmanRegistrationActivity;
import com.example.smts.local_sql.DBHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Products_List_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Context context;
    AdapterProducts sales_adapter;
    ListView lvlistview;
    String TAG = "TAG";
    TextView tvNodata;
    TextView newUser;
    ImageButton menu;
    EditText id;
    DBHelper db;

    MBottomBar mBottomBar;
    private boolean isOpenMenu2 = false;
    int count = 0;

    FirebaseRecyclerOptions options;
    RecyclerView recyclerView;
    DatabaseReference ref;
    FirebaseDatabase database;
    ArrayList<ModelProducts> listData;
    public AdapterProducts adapter;
    ProgressDialog mProg;
  
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        /*
        menu = findViewById(R.id.menu);*/
        mBottomBar = new MBottomBar(findViewById(R.id.home_bottom_bar));
        setFabClick();
        setBarItemClick();
        db = new DBHelper(this);
        id = findViewById(R.id.et_id);
        tvNodata = findViewById(R.id.tvDATA);

        mProg = new ProgressDialog(this);
        mProg.setTitle("Wait Please..");
        mProg.setMessage("Loading Data");
        mProg.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProg.dismiss();
            }
        }, 4000);

        newUser = findViewById(R.id.tv_newuser);
        Log.d(TAG, "onCreate:ShowSalesmanList_Activity ");


        recyclerView = findViewById(R.id.rv_Productslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<ModelProducts>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ProductsTable"), ModelProducts.class)
                .build();
        adapter=new AdapterProducts(options);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("ProductsTable");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UpdateNavHeader();
    }

    @Override
    public void onStart() {
        super.onStart();


        listData=new ArrayList<>();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Toast.makeText(context, "Salesman Registered Successfully", Toast.LENGTH_SHORT).show();
                count++;

                if(count >= snapshot.getChildrenCount()){
                    //stop progress bar here
                    mProg.dismiss();
                    tvNodata.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ModelProducts removedSaleman = snapshot.getValue(ModelProducts.class);

                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).getProduct_id().equals(removedSaleman.getProduct_id())) {
                        listData.remove(i);
                        adapter.notifyItemRemoved(i);
//                        Toast.makeText(Products_List_Activity.this, "Salesman Data Deleted successfully", Toast.LENGTH_LONG).show();

                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        ModelProducts dataItem = npsnapshot.getValue(ModelProducts.class);
                        listData.add(dataItem);

                    }
                    if (listData.isEmpty())
                        tvNodata.setVisibility(View.VISIBLE);
                    else {
                        tvNodata.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            Toast.makeText(Products_List.this, "press Back again to exit ", Toast.LENGTH_SHORT).show();
            super.onBackPressed();

            finish();
            Intent salesman = new Intent(Products_List_Activity.this, Salesman.class);
            startActivity(salesman);

        }
    }


    private void setFabClick() {
        mBottomBar.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenMenu2 = !isOpenMenu2;
                changeBarMenu();
            }
        });
    }

    private void changeBarMenu() {
        if (isOpenMenu2) {
            mBottomBar.getBottomBar().setNavigationIcon(null);
            mBottomBar.getBottomBar().setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            mBottomBar.getBottomBar().replaceMenu(R.menu.bottom_bar_menu_2);
            mBottomBar.fabAnimation(isOpenMenu2);
        } else {
            mBottomBar.getBottomBar().setNavigationIcon(R.drawable.ic_menu);
            mBottomBar.getBottomBar().setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            mBottomBar.getBottomBar().replaceMenu(R.menu.bottom_bar_menu);
            mBottomBar.fabAnimation(isOpenMenu2);
        }
    }

    private void setBarItemClick() {
        mBottomBar.getBottomBar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

//                    case R.id.bar_favorite:
//                        Toast.makeText(Product.this, "Bar Favorite Click", Toast.LENGTH_LONG).show();
//                        break;
                    case R.id.bar_add_sale:
                        finish();
                        Intent i = new Intent(Products_List_Activity.this, SalesmanRegistrationActivity.class);
                        startActivity(i);
                        break;
                    case R.id.bar_add_prod:
                        finish();
                        Intent i2 = new Intent(Products_List_Activity.this, AddProductActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.bar_add_cust:
//                        finish();
//                        Intent i3 = new Intent(Products_List.this, CustomerRegistrationActivity.class);
//                        startActivity(i3);break;
                    case R.id.bar_order:

                        finish();
                        Intent i4 = new Intent(Products_List_Activity.this, Orders_list_activity.class);
                        startActivity(i4);
//                        Toast.makeText(Products_List_Activity.this, "Bar Shopping Click", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.bar_more:
                        Toast.makeText(Products_List_Activity.this, "Bar More Click", Toast.LENGTH_LONG).show();
                        break;
                }

                return false;
            }
        });/*
        final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);*/
        NavigationView navigationView = findViewById(R.id.nav_view);
        mBottomBar.getBottomBar().setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);
            }

        });
        navigationView.setNavigationItemSelectedListener(this);
        context = this;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        switch (item.getItemId()) {

            case R.id.nav_salesman:
                finish();
                Intent i2 = new Intent(Products_List_Activity.this, Salesman.class);
                startActivity(i2);
                break;
//            case R.id.nav_customer:
//
////                finish();
////                Intent i3 = new Intent(Products_List.this, Customer.class);
////                startActivity(i3);
//                break;
            case R.id.nav_order:
                finish();
                Intent i4 = new Intent(Products_List_Activity.this, Orders_list_activity.class);
                startActivity(i4);
                break;

            case R.id.nav_product:
                finish();
                Intent i5 = new Intent(Products_List_Activity.this, Products_List_Activity.class);
                startActivity(i5);
                break;
            case R.id.nav_logOut:
                mAuth.signOut();
                finish();
                Intent i6 = new Intent(Products_List_Activity.this, UserTypeSelection_Activity.class);
                startActivity(i6);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void UpdateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerview = navigationView.getHeaderView(0);
        TextView navUserName = headerview.findViewById(R.id.nav_username);
        TextView navEmail = headerview.findViewById(R.id.nav_email);
        ImageView navProfileDP = headerview.findViewById(R.id.nav_iv_user_dp_id);

        navEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).centerCrop().into(navProfileDP);
    }
}
