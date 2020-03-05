package com.benmohammad.masmobius.taskdetail.view;

import android.view.View;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.TaskDetails;
import com.benmohammad.masmobius.task.view.TaskViewDataMapper;

import static com.benmohammad.masmobius.taskdetail.view.TaskDetailViewData.*;
import static com.google.common.base.Strings.isNullOrEmpty;

public class TaskDetailViewDataMapper {

    public static TaskDetailViewData taskToTaskDetails(Task task) {
        TaskDetails details = task.details();
        String title = details.title();
        String description = details.description();

        return builder()
                .title(TextViewData.create(isNullOrEmpty(title) ? View.GONE : View.VISIBLE, title))
                .description(TextViewData.create(isNullOrEmpty(description) ? View.GONE : View.VISIBLE, description))
                .completedChecked(details.completed())
                .build();

    }
}
