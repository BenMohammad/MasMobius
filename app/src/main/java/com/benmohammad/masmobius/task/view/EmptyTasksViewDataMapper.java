package com.benmohammad.masmobius.task.view;

import android.view.View;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.task.domain.TaskFilters;
import com.benmohammad.masmobius.task.domain.TasksFilterType;
import com.benmohammad.masmobius.task.view.TasksListViewData.EmptyTaskViewData;

public class EmptyTasksViewDataMapper {

    public static EmptyTaskViewData createEmptyTaskViewData(TasksFilterType filter) {
        EmptyTaskViewData.Builder builder =
                EmptyTaskViewData.builder();

        switch(filter) {
            case ACTIVE_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_active)
                        .icon(R.drawable.ic_check_circle)
                        .build();

            case COMPLETED_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_completed)
                        .icon(R.drawable.ic_verified)
                        .build();

            default:
                return builder
                        .addViewVisibility(View.VISIBLE)
                        .title(R.string.no_tasks_all)
                        .icon(R.drawable.ic_assignment)
                        .build();


        }
    }
}
