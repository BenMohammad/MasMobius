package com.benmohammad.masmobius.stats.effecthandlers;

import android.content.Context;

import com.benmohammad.masmobius.data.source.local.TasksLocalDataSource;
import com.benmohammad.masmobius.stats.domain.StatisticsEffect;
import com.benmohammad.masmobius.stats.domain.StatisticsEvent;
import com.benmohammad.masmobius.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

import static com.benmohammad.masmobius.stats.domain.StatisticsEffect.*;
import static com.benmohammad.masmobius.stats.domain.StatisticsEvent.*;

public class StatisticsEffectHandlers {

    public static ObservableTransformer<StatisticsEffect, StatisticsEvent> createEffectHandler(Context context) {
        TasksLocalDataSource localSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<StatisticsEffect, StatisticsEvent> subtypeEffectHandler()
                .addTransformer(LoadTasks.class, loadTasksHandler(localSource))
                .build();
    }

    private static ObservableTransformer<LoadTasks, StatisticsEvent> loadTasksHandler(TasksLocalDataSource localSource) {
        return effects -> effects.flatMap(
                loadTasks ->
                        localSource
                .getTasks()
                .toObservable()
                .take(1)
                .map(ImmutableList::copyOf)
                .map(StatisticsEvent::tasksLoaded)
                .onErrorReturnItem(taskLoadingFailed()));
    }
}
