package com.benmohammad.masmobius.stats.domain;

import androidx.annotation.NonNull;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.First;
import com.spotify.mobius.Next;

import static com.benmohammad.masmobius.stats.domain.StatisticsEffect.loadTasks;
import static com.benmohammad.masmobius.stats.domain.StatisticsState.failed;
import static com.benmohammad.masmobius.stats.domain.StatisticsState.loaded;
import static com.benmohammad.masmobius.stats.domain.StatisticsState.loading;
import static com.spotify.mobius.Effects.effects;
import static com.spotify.mobius.First.first;
import static com.spotify.mobius.Next.next;

public final class StatisticsLogic {

    private StatisticsLogic(){}

    @NonNull
    public static First<StatisticsState, StatisticsEffect> init(StatisticsState state) {
        return state.map(
            loading -> first(state, effects(loadTasks())),
            First::first,
            failed -> first(loading(), effects(loadTasks())));
    }

    @NonNull
    public static Next<StatisticsState, StatisticsEffect> update(StatisticsState state, StatisticsEvent event) {
        return event.map(
                tasksLoaded -> {
                    ImmutableList<Task> tasks = tasksLoaded.tasks();
                    int activeCount = 0;
                    int completedCount = 0;
                    for(Task task : tasks) {
                        if(task.details().completed())completedCount++;
                        else activeCount++;
                    }
                    return next(loaded(activeCount, completedCount));
                },
                taskLoadingFailed -> next(failed()));
    }
}
