package com.example.smts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.smts.salesman.Salesman;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.smts.UserTypeSelection_Activity.isNetworkAvailable;

public class AdminSignUpActivity extends AppCompatActivity {

    static int PReqCode = 1;
    static int REQUESCODE = 1;
    EditText etemail, etpassword, etCpassword, et_username;
    Button btnsubmit;
    TextView newuser;
    ImageView Userpic;
    StorageReference storageReference;
    ProgressBar progressBar;LinearLayout linearLayout;
    Uri uriprofileImg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_sign_up);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        et_username = findViewById(R.id.et_username);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        etCpassword = findViewById(R.id.etCpassword);
        btnsubmit = findViewById(R.id.btn_newsubmit);
        Userpic = findViewById(R.id.iv_pic);
        progressBar = findViewById(R.id.progbar);
        newuser = findViewById(R.id.tv_newuser);
        linearLayout = findViewById(R.id.liner);

        Userpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_username.getText().toString().trim();
                final String email = etemail.getText().toString().trim();
                final String password = etpassword.getText().toString().trim();
                final String Cpassword = etCpassword.getText().toString().trim();
                if (uriprofileImg == null) {

                    Toast.makeText(AdminSignUpActivity.this, "please upload admin profile picture..!", Toast.LENGTH_SHORT).show();

                    return;
                } else if (name.isEmpty()) {
                    et_username.setError("enter username");
                    et_username.requestFocus();
                    return;
                } else if (email.isEmpty()) {
                    etemail.setError("enter valid email");
                    etemail.requestFocus();
                    return;
                } else if (password.isEmpty() || password.length() < 6) {
                    etpassword.setError("enter at least 6 digits/characters new password");
                    etCpassword.setError("password ");
                    etpassword.requestFocus();
                    return;
                } else if (Cpassword.isEmpty()) {
                    etCpassword.setError("enter password again");
                    return;
                } else if (!password.equals(Cpassword)) {
                    etCpassword.setError("password not match");
                    etCpassword.requestFocus();
                    return;
                } else {

                    if (isNetworkAvailable(AdminSignUpActivity.this)) {
                        linearLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        createNewUser(email, name, password);
                    }else { UserTypeSelection_Activity.openWifISettings(AdminSignUpActivity.this); }
                }
                btnsubmit.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESCODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //user sucefuuky pick an img
            //now we have to save its prefrence to uri var
            uriprofileImg = data.getData();

            Glide.with(this).load(uriprofileImg).centerCrop().into(Userpic);
//            Userpic.setImageURI(uriprofileImg);
        }
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AdminSignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AdminSignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(AdminSignUpActivity.this, "pleasw accept permissioin", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AdminSignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        //opens gallery and waint untilnuser pick up img
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, REQUESCODE);

    }

    private void createNewUser(String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MSG", "createUserWithEmail:success");
                            Toast.makeText(AdminSignUpActivity.this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.VISIBLE);
                            saveUserInfo(name, uriprofileImg, mAuth.getCurrentUser());
                            UpdateUI();
                            /*Intent salesman1 = new Intent(AdminSignUpActivity.this, Salesman.class);
                            startActivity(salesman1);*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MSG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AdminSignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                            btnsubmit.setVisibility(View.VISIBLE);
                        }
                        // ...
                    }

                });
    }


    private void saveUserInfo(final String Name, Uri uriprofileImg, final FirebaseUser currentUser) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference mstorageRef = FirebaseStorage.getInstance().getReference().child("users_DP");
        final StorageReference imgFilePAth = mstorageRef.child(uriprofileImg.getLastPathSegment());
        imgFilePAth.putFile(uriprofileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //img uploaded sucefuly
            //nnow get download our IMG url
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgFilePAth.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //url contain IMG url
                        UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(Name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    UpdateUI();progressBar.setVisibility(View.VISIBLE);
//                                    Toast.makeText(AdminSignUpActivity.this, "account is created.",
//                                            Toast.LENGTH_SHORT).show();
                                }else {
                                    UpdateUI();progressBar.setVisibility(View.VISIBLE);

                                    // If sign in fails, display a message to the user.
                                    Log.w("MSG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(AdminSignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    btnsubmit.setVisibility(View.VISIBLE);
                                }
                            }

                        });

                    }
                });

            }
        });
    }

    private void UpdateUI() { finish();
        Intent salesman = new Intent(AdminSignUpActivity.this, Salesman.class);
        startActivity(salesman);

    }
}

