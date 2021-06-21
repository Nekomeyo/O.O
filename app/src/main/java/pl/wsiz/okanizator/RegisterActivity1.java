package pl.wsiz.okanizator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity1 extends AppCompatActivity {

    ImageView userAvatar;
    static int PReqCode=1;
    static int REQUESTCODE=1;
    Uri pickedUri;

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword1;
    private EditText userPassword2;
    private ProgressBar loadingProgressBar;
    private Button regBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = (EditText) this.findViewById(R.id.regName);
        userEmail = (EditText) this.findViewById(R.id.regEmail);
        userPassword1 = (EditText) this.findViewById(R.id.regPass1);
        userPassword2 = (EditText) this.findViewById(R.id.regPass2);
        loadingProgressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        regBtn = (Button) this.findViewById(R.id.registerbtn);

        loadingProgressBar.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgressBar.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password1 = userPassword1.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if( email.isEmpty()||name.isEmpty()||password2.isEmpty()||!password1.equals(password2)){

                    showMessage("Niepoprawne dane");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }else{

                    CreateUserAccount(email,name,password1);
                }


            }
        });

        userAvatar=findViewById(R.id.nav_useravatar);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    checkAndRequestForPermission();
                }else
                {
                    openGallery();
                }


            }
        });
    }

    private void CreateUserAccount(String email, final String name, String password1) {
mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){

            showMessage("Konto zostało utworzone");

    if(pickedUri!=null){

            updateUserInfo( name,pickedUri,mAuth.getCurrentUser());}else{
        updateUserInfoWithoutPhoto(name,mAuth.getCurrentUser());
    }

        }else{
            showMessage("Utworzenie konta się nie powiodło"+task.getException().getMessage());
            regBtn.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }
});





    }

private void updateUserInfo(final String name, Uri pickedUri, final FirebaseUser currentUser){
    StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
    final StorageReference imageFilePath = mStorage.child(pickedUri.getLastPathSegment());
    imageFilePath.putFile(pickedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            //załadowane zdjęcie do firebase
           imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                   //uri ma zdjęcie
                   UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                   currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()){
                               showMessage("Rejestracja zakończona");
                               updateUI();
                           }else{
                               showMessage("Coś sie spsuło");
                           }
                       }
                   });
               }
           });
        }
    });
}
    private void updateUserInfoWithoutPhoto(final String name, final FirebaseUser currentUser){

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    showMessage("Rejestracja zakończona");
                                    updateUI();
                                }else{
                                    showMessage("Coś sie spsuło");
                                }
                            }
                        });
                    }




    private void updateUI() {
        Intent TestActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(TestActivity);
        finish();
    }
    private void updateUIback() {
        Intent LoginActivity1 = new Intent(getApplicationContext(), LoginActivity1.class);
        startActivity(LoginActivity1);
        finish();
    }


    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    private void checkAndRequestForPermission() {
if(ContextCompat.checkSelfPermission(RegisterActivity1.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
    if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity1.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
       showMessage("Dostęp wymagany");
    }else{
        ActivityCompat.requestPermissions(RegisterActivity1.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
    }
}else{
    openGallery();
}
    }
    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUESTCODE&&data!=null){

            pickedUri=data.getData();
            userAvatar.setImageURI(pickedUri);
        }
    }
    @Override
    public void onBackPressed() {
            updateUIback();
        }



}
