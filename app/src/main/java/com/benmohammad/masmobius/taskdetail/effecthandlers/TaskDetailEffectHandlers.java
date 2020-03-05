package com.benmohammad.masmobius.taskdetail.effecthandlers;

import android.content.Context;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.source.TasksDataSource;
import com.benmohammad.masmobius.data.source.local.TasksLocalDataSource;
import com.benmohammad.masmobius.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.DeleteTask;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.NotifyTaskMarkedComplete;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.OpenTaskEditor;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.SaveTask;
import com.benmohammad.masmobius.taskdetail.domain.TaskDetailEvent;
import com.benmohammad.masmobius.taskdetail.view.TaskDetailViewActions;
import com.benmohammad.masmobius.util.schedulers.SchedulerProvider;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.benmohammad.masmobius.taskdetail.domain.TaskDetailEffect.*;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TaskDetailEffectHandlers {

    public static ObservableTransformer<TaskDetailEffect, TaskDetailEvent> createEffectHandlers(
            TaskDetailViewActions views, Context context, Action dismiss, Consumer<Task> launchEditor
    ) {

        TasksRemoteDataSource remoteDataSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource  localDataSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<TaskDetailEffect, TaskDetailEvent> subtypeEffectHandler()
                .addFunction(DeleteTask.class, deleteTaskHandler(remoteDataSource, localDataSource))
                .addFunction(SaveTask.class, saveTaskHandler(remoteDataSource, localDataSource))
                .addAction(NotifyTaskMarkedComplete.class, views::showTaskMarkedComplete, mainThread())
                .addAction(NotifyTaskMarkedActive.class, views::showTaskMarkedActive, mainThread())
                .addAction(NotifyTaskDeletionFailed.class, views::showTaskDeletionFailed, mainThread())
                .addAction(NotifyTaskSaveFailed.class, views::showTaskSavingFailed, mainThread())
                .addConsumer(OpenTaskEditor.class, openTaskEditorHandler(launchEditor), mainThread())
                .addAction(Exit.class, dismiss, mainThread())
                .build();

    }

    private static Consumer<OpenTaskEditor> openTaskEditorHandler(Consumer<Task> launchEditorCommand) {
        return openEditorEffect -> launchEditorCommand.accept(openEditorEffect.task());
    }

    private static Function<SaveTask, TaskDetailEvent> saveTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        return saveTask -> {
            try {
                remoteSource.saveTask(saveTask.task());
                localSource.saveTask(saveTask.task());
                return saveTask.task().details().completed() ? TaskDetailEvent.taskMarkedComplete() : TaskDetailEvent.taskMarkedActive();
            } catch (Exception e) {

                return TaskDetailEvent.taskSaveFailed();
            }
        };
    }

    private static Function<DeleteTask, TaskDetailEvent> deleteTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        return deleteTask -> {
            try {
                remoteSource.deleteTask(deleteTask.task().id());
                localSource.deleteTask(deleteTask.task().id());
                return TaskDetailEvent.taskDeleted();
            } catch (Exception e) {
                return TaskDetailEvent.taskDeletionFailed();
            }
        };
    }

}

