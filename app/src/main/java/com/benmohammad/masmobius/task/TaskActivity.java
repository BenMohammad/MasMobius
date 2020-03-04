package com.benmohammad.masmobius.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.util.ActivityUtils;
import com.google.android.material.navigation.NavigationView;

public class TaskActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if(navigationView != null) {
            setUpDrawerContent(navigationView);
        }

        TaskFragment taskFragment =
                (TaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(taskFragment == null) {
            taskFragment = TaskFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch(menuItem.getItemId()) {

                        case R.id.list_navigation_menu_item:
                        break;

                        case R.id.statistics_navigation_menu_item:
                            //Intent intent = new Intent(TaskActivity.this, StatisticsActivity.class);
                            break;

                        default:
                            break;
                    }

                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;

                }
        );
    }

}
