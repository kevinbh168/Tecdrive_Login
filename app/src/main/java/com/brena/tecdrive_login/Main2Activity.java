package com.brena.tecdrive_login;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.brena.tecdrive_login.R;
import com.brena.tecdrive_login.utils.Tools;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class Main2Activity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private MenuView.ItemView sign_Out;
    private TextView fullNameText;
    private TextView email;
    private ImageView photoProfile;

    //MATERIAL X
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Menu menu_navigation;
    private DrawerLayout drawer;
    private View navigation_header;
    private boolean is_account_mode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //capturas de los componentes en la navegacion lateral
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        fullNameText = headerView.findViewById(R.id.fullNameText);
        email = headerView.findViewById(R.id.email);
        photoProfile = headerView.findViewById(R.id.avatar_user);
        sign_Out = headerView.findViewById(R.id.nav_sign_out);


        //Codigo de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Main2Activity.this);
        if (acct != null) {
            String personEmail = acct.getEmail();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            Uri personPhoto = acct.getPhotoUrl();
            String firstWord = personGivenName.replaceAll(" .*", "");
            String fullName = firstWord + " " + personFamilyName;

            fullNameText.setText(fullName);
            email.setText(personEmail);
            Glide.with(this).load(personPhoto).into(photoProfile);
        }

        initToolbar();
        initNavigationMenu(acct);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(Main2Activity.this, "Cerrando Sesion ...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main2Activity.this, MainActivity.class));
                        finish();

                    }
                });

    }

    //CLASES MATERIAL X

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("TecDrive");
    }

    private void initNavigationMenu(final GoogleSignInAccount acct) {
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();


                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        fragmentManager.beginTransaction().replace(R.id.drawer_content,new HomeFragment()).commit();
                        //Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
                        actionBar.setTitle(item.getTitle());
                        drawer.closeDrawers();
                        break;
                    }
                    case R.id.nav_profile: {
                        //fragmentManager.beginTransaction().replace(R.id.drawer_content, new PerfilFragment()).commit();

                        Log.d("nav_profile",acct.getDisplayName());
                        PerfilFragment profileFragment=new PerfilFragment();
                        profileFragment.PerfilFragment(acct);
                        fragmentManager.beginTransaction().replace(R.id.drawer_content,profileFragment).commit();
                       // fragmentManager.beginTransaction().replace(Contenedor de fragment,fragmento puede ser new como el que ya se esta usando).commit();


                        actionBar.setTitle(item.getTitle());
                        drawer.closeDrawers();
                        break;
                    }
                    case R.id.nav_sign_out: {
                        drawer.closeDrawers();
                        Toast.makeText(getApplicationContext(), "Cerrando Sesion", Toast.LENGTH_SHORT).show();
                        signOut();
                    }
                }
                return true;
            }
        });

        // open drawer at start
        drawer.openDrawer(GravityCompat.START);
    }
}

