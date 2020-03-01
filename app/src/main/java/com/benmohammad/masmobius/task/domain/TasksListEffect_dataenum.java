package com.benmohammad.masmobius.task.domain;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface TasksListEffect_dataenum {

    dataenum_case RefreshTasks();
    dataenum_case LoadTasks();
    dataenum_case SaveTask(Task task);
    dataenum_case DeleteTasks(ImmutableList<Task> tasks);
    dataenum_case ShowFeedback(FeedbackType feedbackType);
    dataenum_case NavigateToTaskDetails(Task task);
    dataenum_case StartTaskCreationFlow();
}
