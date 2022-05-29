package com.example.socialmediagamer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.fragments.ChatsFragment;
import com.example.socialmediagamer.fragments.FiltersFragment;
import com.example.socialmediagamer.fragments.HomeFragment;
import com.example.socialmediagamer.fragments.ProfileFragment;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.example.socialmediagamer.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        //bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.itemHome){
                    //FRAGMENT HOME
                    openFragment(new HomeFragment());
                }
                else if(item.getItemId() == R.id.itemChats){
                    //FRAGMENT CHATS
                    openFragment(new ChatsFragment());
                }
                else if(item.getItemId() == R.id.itemProfile){
                    //Fragmetno profile
                    openFragment(new ProfileFragment());
                }
                return  true;
            }
        });
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        openFragment(new HomeFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
    }

    private void updateOnline(boolean status) {
        mUserProvider.updateOnline(mAuthProvider.getUid(), status );
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}