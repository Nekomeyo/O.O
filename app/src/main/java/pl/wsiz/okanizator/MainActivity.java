package pl.wsiz.okanizator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import pl.wsiz.okanizator.ui.home.ui.main.SectionsPagerAdapter;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

private TabLayout tabLayout;
private ViewPager viewPager;
private SectionsPagerAdapter adapter;


    private DrawerLayout drawer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateNavHeader();




        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new TodoFragment(), "Do Zrobienia");
        adapter.AddFragment(new InprogressFragment(), "W trakcie");

        adapter.AddFragment(new DoneFragment(), "Ukończone");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = findViewById(R.id.add_todo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 updateUI();


                }

        });


    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateNavHeader() {

        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navUserEmail = headerView.findViewById(R.id.nav_useremail);
        ImageView userAvatar = headerView.findViewById(R.id.nav_useravatar);
        navUserName.setText(currentUser.getDisplayName());
        navUserEmail.setText(currentUser.getEmail());

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userAvatar);
        } else {
            Glide.with(this).load(R.drawable.userphoto).into(userAvatar);
        }

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity1.class);
            startActivity(loginActivity);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void updateUI() {
        Intent NewActivity = new Intent(getApplicationContext(), NewActivity1.class);
        startActivity(NewActivity);
        finish();
    }

}
