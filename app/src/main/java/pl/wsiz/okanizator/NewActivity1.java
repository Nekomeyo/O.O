package pl.wsiz.okanizator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static final String KEY_TITLE ="title";
    private static final String KEY_DESC = "desc";
    private EditText etItem;
    private Button saveItemBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference todoRef = db.collection("ToDo");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Toolbar toolbar = findViewById(R.id.toolbar); // toolbar staje sie action bar
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        /////////////////////////////// do itemow i sign out /////////////
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

etItem=findViewById(R.id.editTextItem);
saveItemBtn=findViewById(R.id.saveItemBtn);
saveItemBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

            saveNote(view);



    }
});
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateNavHeader();


    }

        public void saveNote(View view) {
        String textitem = etItem.getText().toString();
        String email = currentUser.getEmail();



            if(textitem.equals("")){
                showMessage("Napisz co masz w planie?");

}else {

    Item item = new Item(textitem, email);
    todoRef.add(item).addOnSuccessListener(new OnSuccessListener() {
                                               @Override
                                               public void onSuccess(Object o) {
                                                   showMessage("Zapisane");
                                               }

                                           }
    ).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            showMessage("Coś poszło nie tak");
            Log.d("Firebase add", e.toString());
        }
    });
}

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity1.class);
            startActivity(loginActivity);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
    public void updateNavHeader() {
        //czy tu nie trzeba jak w poprzednich this?
        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navUserEmail = headerView.findViewById(R.id.nav_useremail);
        ImageView userAvatar = headerView.findViewById(R.id.nav_useravatar);
        navUserName.setText(currentUser.getDisplayName());
        navUserEmail.setText(currentUser.getEmail());
        // Glide dla obrazka
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userAvatar);
        } else {
            Glide.with(this).load(R.drawable.userphoto).into(userAvatar);
        }

    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            updateUI();
            super.onBackPressed();
        }
    }
    private void updateUI() {
        Intent testActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(testActivity);
        finish();
}
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


}