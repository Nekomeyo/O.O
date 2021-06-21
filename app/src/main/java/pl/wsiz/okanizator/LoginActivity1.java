package pl.wsiz.okanizator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity1 extends AppCompatActivity {

    private EditText userEmail,userPassword;
    private Button loginBtn, googleLoginBtn;
    private ProgressBar loginProgBar;
    private ImageView avatar;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 10 ;

    private Intent TestActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userEmail = (EditText) this.findViewById(R.id.loginEmail);
        userPassword = (EditText) this.findViewById(R.id.loginPas);
        loginProgBar = (ProgressBar) this.findViewById(R.id.loginProgBar);
        loginBtn = (Button) this.findViewById(R.id.loginBtn);
        avatar = (ImageView) this.findViewById(R.id.loginAvatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regActivity = new Intent(getApplicationContext(), RegisterActivity1.class);
                startActivity(regActivity);
                finish();
            }
        });

        mAuth=FirebaseAuth.getInstance();

        TestActivity = new Intent(this, MainActivity.class);

        loginProgBar.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);
                googleLoginBtn.setVisibility(View.INVISIBLE);

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                if(mail. isEmpty()||password.isEmpty()){
                    showMessage("Pola są wypełnionie niepoprawnie");
                    loginProgBar.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    googleLoginBtn.setVisibility(View.VISIBLE);
                }else{
                    signInWithPassword(mail,password);
                }
            }
        });
        googleLoginBtn = (Button) this.findViewById(R.id.GoogleBtn);
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgBar.setVisibility(View.VISIBLE);
                googleLoginBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);
                createRequest();
                googleSignIn();

            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest() {
// .requestIdToken(getString(R.string.default_web_client_id))
        // https://console.cloud.google.com/apis/credentials?project=okanizator&orgonly=true&supportedpurview=organizationId
        //web client
        // w res values, a nie string

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                showMessage(e.getMessage());
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginProgBar.setVisibility(View.INVISIBLE);
                            googleLoginBtn.setVisibility(View.VISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                            updateUI();

                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("Autentykacja nie powiodła się"+task.getException().getMessage());
                            loginBtn.setVisibility(View.VISIBLE);
                            googleLoginBtn.setVisibility(View.VISIBLE);
                            loginProgBar.setVisibility(View.INVISIBLE);

                        }

                    }
                });
    }

    private void signInWithPassword(String mail, String password) {
mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
            loginProgBar.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            googleLoginBtn.setVisibility(View.VISIBLE);
            updateUI();


        }else{
            showMessage("Logowanie się nie powiodło"+task.getException().getMessage());
            loginBtn.setVisibility(View.VISIBLE);
            googleLoginBtn.setVisibility(View.VISIBLE);
            loginProgBar.setVisibility(View.INVISIBLE);
        }
    }
});
    }

    private void updateUI() {
        startActivity(TestActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){

            updateUI();
        }
    }
}
