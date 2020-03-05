package com.benmohammad.masmobius.addedittask.domain;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.TaskDetails;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface AddEditTaskEffect_dataenum {

    dataenum_case NotifyEmptyTaskNotAllowed();
    dataenum_case CreateTask(TaskDetails taskDetails);
    dataenum_case SaveTask(Task task);
    dataenum_case Exit(boolean successful);
}
