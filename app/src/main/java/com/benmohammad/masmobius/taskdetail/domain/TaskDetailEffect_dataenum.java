package com.benmohammad.masmobius.taskdetail.domain;

import com.benmohammad.masmobius.data.Task;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface TaskDetailEffect_dataenum {

    dataenum_case DeleteTask(Task task);
    dataenum_case SaveTask(Task task);
    dataenum_case NotifyTaskMarkedComplete();
    dataenum_case NotifyTaskMarkedActive();
    dataenum_case NotifyTaskSaveFailed();
    dataenum_case NotifyTaskDeletionFailed();
    dataenum_case OpenTaskEditor();
    dataenum_case Exit();
}