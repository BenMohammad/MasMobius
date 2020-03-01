package com.benmohammad.masmobius.task.view;

import androidx.annotation.Nullable;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.task.domain.TaskFilters;
import com.benmohammad.masmobius.task.domain.TasksFilterType;
import com.benmohammad.masmobius.task.domain.TasksListModel;
import com.google.common.collect.ImmutableList;

import static com.benmohammad.masmobius.task.view.EmptyTasksViewDataMapper.createEmptyTaskViewData;
import static com.benmohammad.masmobius.task.view.ViewState.awaitingTasks;
import static com.benmohammad.masmobius.task.view.ViewState.emptyTasks;
import static com.benmohammad.masmobius.task.view.ViewState.hasTasks;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.*;

public class TasksListViewDataMapper {

    public static TasksListViewData tasksListModelToViewData(TasksListModel model){
        return TasksListViewData.builder()
                .loading(model.loading())
                .filterLabel(getFilterLabel(model.filter()))
                .viewState(getViewState(model.tasks(), model.filter()))
                .build();
    }

    private static ViewState getViewState(@Nullable ImmutableList<Task> tasks, TasksFilterType filter) {
        if(tasks == null) return awaitingTasks();
        ImmutableList<Task> filteredTasks = TaskFilters.filterTasks(tasks, filter);
        if(filteredTasks.isEmpty()) {
            return emptyTasks(createEmptyTaskViewData(filter));
        } else {
            return hasTasks(copyOf(transform(filteredTasks, TaskViewDataMapper::createTasksViewData)));
        }
    }

    private static int getFilterLabel(TasksFilterType filterType) {
        switch(filterType) {
            case ACTIVE_TASKS:
                return R.string.label_active;

            case COMPLETED_TASKS:
                return R.string.label_completed;

            default:
            case ALL_TASKS:
                return R.string.label_all;
        }
    }

}
