package com.benmohammad.masmobius.task.domain;

import android.os.Bundle;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

import static com.benmohammad.masmobius.data.TaskBundlePacker.taskFromBundle;
import static com.benmohammad.masmobius.data.TaskBundlePacker.taskToBundle;

public class TasksListModelBundlePacker {

    private static class TasksListModelBundleIdentifiers {
        static final String FILTER = "model_filter";
        static final String LOADING = "model_loading";
        static final String TASKS = "model_tasks";
    }

    public static Bundle tasksListsModelToBundle(TasksListModel model) {
        Bundle b = new Bundle();
        b.putSerializable(TasksListModelBundleIdentifiers.FILTER, model.filter());
        b.putBoolean(TasksListModelBundleIdentifiers.LOADING, model.loading());
        ImmutableList<Task> tasks = model.tasks();
        if(tasks != null) {
            ArrayList<Bundle> taskBundles = new ArrayList<>();
            for(Task task : tasks) {
                taskBundles.add(taskToBundle(task));
            }
            b.putParcelableArrayList(TasksListModelBundleIdentifiers.TASKS, taskBundles);
        }
        return b;

    }

    public static TasksListModel tasksListModelFromBundle(Bundle b) {
        TasksListModel.Builder builder =
                TasksListModel.builder()
                .filter((TasksFilterType) b.getSerializable(TasksListModelBundleIdentifiers.FILTER))
                .loading(b.getBoolean(TasksListModelBundleIdentifiers.LOADING));

        ArrayList<Bundle> bundles =b.getParcelableArrayList(TasksListModelBundleIdentifiers.TASKS);
        if(bundles == null) return builder.build();

        ImmutableList.Builder<Task> tasks = ImmutableList.builder();
        for(Bundle bundle : bundles) {
            tasks.add(taskFromBundle(bundle));
        }
        return builder.tasks(tasks.build()).build();
    }

}
