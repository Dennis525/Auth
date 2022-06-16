package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    //TextInputLayout inputUsername,inputPassword;
    ImageView imageView;
    Button button;

    //MaterialButtonToggleGroup toggleGroup;
    TextView view;
    SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.imageLogo);
        button = findViewById(R.id.Sign_Up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });
       /* inputUsername = findViewById(R.id.outlinedTextUsername);
        inputPassword = findViewById(R.id.outlinedTextPassword);
        toggleGroup = findViewById(R.id.toggleButton);*/


       /* toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked){
              if (checkedId == R.id.buttonLogIn){
                  Toast.makeText(this, "Login Clicked", Toast.LENGTH_SHORT).show();
                  clickLogin();
              }else if (checkedId == R.id.buttonSignUp){
                  Toast.makeText(this, "SignUp Clicked", Toast.LENGTH_SHORT).show();
                  startActivity(new Intent(MainActivity.this, SignUp.class));
              } else if (checkedId == R.id.buttonForget){
                  Toast.makeText(this, "Forget Clicked", Toast.LENGTH_SHORT).show();
              }
            }
        });*/

        connectionStatus();

        view = findViewById(R.id.textView);
        signInButton = findViewById(R.id.googleSign_in);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }
    //check if there is internet connection
    private void connectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (null!= networkInfo){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType()== ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this, "Internet connection successful", Toast.LENGTH_SHORT).show();

            }
        }
        else
            Toast.makeText(this, "No internet connection.Please turn on your mobile data or wifi connection", Toast.LENGTH_SHORT).show();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RESULT_CANCELED){
            if (requestCode == RC_SIGN_IN){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                manageSignInResult(task);
            }
        }
    }

    private void manageSignInResult(Task<GoogleSignInAccount> doneTask) {
        try {
            GoogleSignInAccount account = doneTask.getResult(ApiException.class);
            Toast.makeText(this, "SignIn Successfully", Toast.LENGTH_SHORT).show();
            SignUpActivity();
            FirebaseGoogleAuth(account);
        } catch (ApiException exception){
            Toast.makeText(this, doneTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Firebase Successful", Toast.LENGTH_SHORT).show();

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    updateUI(firebaseUser);
                }
                else {
                    Toast.makeText(MainActivity.this, "Firebase Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void SignUpActivity() {
        Intent intent = new Intent(getApplicationContext(),Welcome.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser firebaseUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            String personalName = account.getDisplayName();
            String personalGiveName = account.getGivenName();
            String personalFamilyName = account.getFamilyName();
            String personalEmail = account.getEmail();
            String personalId = account.getId();
            Uri personalPhoto = account.getPhotoUrl();

            Toast.makeText(this, personalName + personalEmail, Toast.LENGTH_SHORT).show();
        }
    }
}