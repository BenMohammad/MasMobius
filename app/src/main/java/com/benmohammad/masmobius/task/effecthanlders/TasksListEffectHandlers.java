package com.benmohammad.masmobius.task.effecthanlders;

import android.content.Context;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.source.TasksDataSource;
import com.benmohammad.masmobius.data.source.local.TasksLocalDataSource;
import com.benmohammad.masmobius.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.masmobius.task.domain.TasksListEffect;
import com.benmohammad.masmobius.task.domain.TasksListEffect.DeleteTasks;
import com.benmohammad.masmobius.task.domain.TasksListEffect.LoadTasks;
import com.benmohammad.masmobius.task.domain.TasksListEffect.NavigateToTaskDetails;
import com.benmohammad.masmobius.task.domain.TasksListEffect.RefreshTasks;
import com.benmohammad.masmobius.task.domain.TasksListEffect.SaveTask;
import com.benmohammad.masmobius.task.domain.TasksListEffect.ShowFeedback;
import com.benmohammad.masmobius.task.domain.TasksListEffect.StartTaskCreationFlow;
import com.benmohammad.masmobius.task.domain.TasksListEvent;
import com.benmohammad.masmobius.task.domain.TasksListModel;
import com.benmohammad.masmobius.task.view.TasksListViewActions;
import com.benmohammad.masmobius.util.Either;
import com.benmohammad.masmobius.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.rx2.RxMobius;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.benmohammad.masmobius.task.domain.TasksListEvent.taskRefreshed;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.tasksLoaded;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.tasksLoadingFailed;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TasksListEffectHandlers {

    public static ObservableTransformer<TasksListEffect, TasksListEvent> createEffectHandler(
            Context context,
            TasksListViewActions view,
            Action showAddTask,
            Consumer<Task> showTaskDetails
    ) {
        TasksRemoteDataSource remoteSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<TasksListEffect, TasksListEvent> subtypeEffectHandler()
                .addTransformer(RefreshTasks.class, refreshTaskHandler(remoteSource, localSource))
                .addTransformer(LoadTasks.class, loadTaskHandler(localSource))
                .addConsumer(SaveTask.class, saveTaskHandler(remoteSource, localSource))
                .addConsumer(DeleteTasks.class, deleteTaskHandler(remoteSource, localSource))
                .addConsumer(ShowFeedback.class, showFeedbackHandler(view), mainThread())
                .addConsumer(NavigateToTaskDetails.class, navigateToDetailsHandler(showTaskDetails), mainThread())
                .addAction(StartTaskCreationFlow.class, showAddTask, mainThread())
                .build();
    }


    static ObservableTransformer<RefreshTasks, TasksListEvent> refreshTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        Single<TasksListEvent> refreshTasksOperation =
                remoteSource
                .getTasks()
                .singleOrError()
                .map(Either::<Throwable, List<Task>> right)
                .onErrorReturn(Either::left)
                .flatMap(
                        either -> either.map(
                                left -> Single.just(tasksLoadingFailed()),
                                right -> Observable.fromIterable(right.value())
                                .concatMapCompletable(
                                        t -> Completable.fromAction(
                                                () -> localSource.saveTask(t)))
                                        .andThen(Single.just(taskRefreshed()))
                                        .onErrorReturnItem(tasksLoadingFailed())));

                        return refreshTasks -> refreshTasks.flatMapSingle(__ -> refreshTasksOperation);

    }

    static ObservableTransformer<LoadTasks, TasksListEvent> loadTaskHandler(TasksDataSource dataSource) {
        return loadTasks -> loadTasks.flatMap(
                effects ->
                        dataSource
                .getTasks()

                .toObservable()
                .take(1)
                .map(tasks ->tasksLoaded(ImmutableList.copyOf(tasks)))
                .onErrorReturnItem(tasksLoadingFailed()));

    }

    static Consumer<SaveTask> saveTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        return saveTasksEffect -> {
            remoteSource.saveTask(saveTasksEffect.task());
            localSource.saveTask(saveTasksEffect.task());
        };
    }

    static Consumer<DeleteTasks> deleteTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        return deleteTasks -> {
            for(Task task : deleteTasks.tasks()) {
                remoteSource.deleteTask(task.id());
                localSource.deleteTask(task.id());
            }
        };
    }

    static Consumer<ShowFeedback> showFeedbackHandler(TasksListViewActions view) {
        return showFeedback -> {
            switch(showFeedback.feedbackType()) {
                case SAVED_SUCCESSFULLY:
                    view.showSuccessfullySavedMessage();
                    break;
                case MARKED_ACTIVE:
                    view.showTaskMarkedActive();
                    break;
                case LOADING_ERROR:
                    view.showLoadingTaskError();
                    break;
                case CLEARED_COMPLETED:
                    view.showCompleteTaskCleared();
                case MARKED_COMPLETE:
                    view.showTaskMarkedComplete();
                    break;
            }
        };
    }

    static Consumer<NavigateToTaskDetails> navigateToDetailsHandler(Consumer<Task> command) {
        return navigateEffect -> command.accept(navigateEffect.task());
    }


}
