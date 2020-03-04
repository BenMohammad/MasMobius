package com.benmohammad.masmobius.task.domain;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskFilters {

    private static final HashMap<TasksFilterType, Predicate<Task>> FILTERS;

    static {
        FILTERS = new HashMap<>();
        FILTERS.put(TasksFilterType.ALL_TASKS, t -> true);
        FILTERS.put(TasksFilterType.ACTIVE_TASKS, t -> !t.details().completed());
        FILTERS.put(TasksFilterType.COMPLETED_TASKS, t -> t.details().completed());
    }

    public static ImmutableList<Task> filterTasks(List<Task> tasks, TasksFilterType filter) {
        return Observable.fromIterable(checkNotNull(tasks))
                .filter(FILTERS.get(filter))// this line is null ------------------------------>>>>
                .toList()
                .map(ImmutableList::copyOf)
                .blockingGet();
    }
}
