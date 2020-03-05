package com.benmohammad.masmobius.taskdetail.effecthandlers;

import android.content.Context;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEvent;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class TaskDetailEffectHandlers {

    public static ObservableTransformer<TaskDetailEffect, TaskDetailEvent> createEffectHandlers(
            //TaskDetailViewActions actions, Context context, Action dismiss, Consumer<Task> lauchEditor
    ) {

        return null;
    }
}
