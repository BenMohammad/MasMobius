package com.benmohammad.masmobius.taskdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.TaskBundlePacker;
import com.benmohammad.masmobius.util.ActivityUtils;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    public static Intent showTask(Context c, Task task) {
        Intent i = new Intent(c, TaskDetailActivity.class);
        i.putExtra(EXTRA_TASK_ID, TaskBundlePacker.taskToBundle(task));
        return i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.taskdetail_act);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab =  getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        Task task = TaskBundlePacker.taskFromBundle(getIntent().getBundleExtra(EXTRA_TASK_ID));
        TaskDetailFragment taskDetailFragment =
                (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(task);

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), taskDetailFragment, R.id.contentFrame
            );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
