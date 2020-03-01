package com.benmohammad.masmobius.task.domain;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface TasksListEvent_dataenum {

    dataenum_case RefreshRequested();
    dataenum_case NewTaskClicked();
    dataenum_case NavigateToTaskDetailsRequested(String taskId);
    dataenum_case TaskMarkedComplete(String taskId);
    dataenum_case TaskMarkedActive(String taskId);
    dataenum_case ClearCompletedTasksRequested();
    dataenum_case FilterSelected(TasksFilterType filterType);
    dataenum_case TasksLoaded(ImmutableList<Task> tasks);
    dataenum_case TaskCreated();
    dataenum_case TaskRefreshed();
    dataenum_case TaskRefreshFailed();
    dataenum_case TasksLoadingFailed();
}
