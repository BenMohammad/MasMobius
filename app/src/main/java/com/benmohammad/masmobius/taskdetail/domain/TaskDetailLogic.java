package com.benmohammad.masmobius.taskdetail.domain;

import androidx.annotation.NonNull;

import com.benmohammad.masmobius.data.Task;
import com.spotify.mobius.Next;

import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.deleteTask;
import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.exit;
import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.notifyTaskMarkedActive;
import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.notifyTaskMarkedComplete;
import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.openTaskEditor;
import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.saveTask;
import static com.spotify.mobius.Effects.effects;
import static com.spotify.mobius.Next.dispatch;
import static com.spotify.mobius.Next.next;
import static com.spotify.mobius.Next.noChange;

public class TaskDetailLogic {

    @NonNull
    public static Next<Task, TaskDetailEffect> update(Task task, TaskDetailEvent event) {
        return event.map(
                deleteTaskRequested -> dispatch(effects(deleteTask(task))),
                completeTaskRequested -> onCompleteTaskRequested(task),
                activateTaskRequested -> onActivateTaskRequested(task),
                editTaskRequested -> dispatch(effects(openTaskEditor(task))),
                taskDeleted -> dispatch(effects(exit())),
                TaskCompleted -> dispatch(effects(notifyTaskMarkedComplete())),
                taskActivated -> dispatch(effects(notifyTaskMarkedActive())),
                taskSaveFailed -> noChange(),
                taskDeletionFailed -> noChange()

        );
    }

    private static Next<Task, TaskDetailEffect> onActivateTaskRequested(Task task) {
        if(!task.details().completed()) {
            return noChange();
        }
        Task activatedTask = task.activate();
        return next(activatedTask, effects(saveTask(activatedTask)));
    }

    private static Next<Task, TaskDetailEffect> onCompleteTaskRequested(Task task) {
        if(task.details().completed()) {
            return  noChange();
        }

        Task completedTask = task.complete();
        return next(completedTask, effects(saveTask(completedTask)));
    }

}
