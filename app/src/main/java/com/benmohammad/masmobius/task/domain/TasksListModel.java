package com.benmohammad.masmobius.task.domain;

import androidx.annotation.Nullable;

import com.benmohammad.masmobius.data.Task;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@AutoValue
public abstract class TasksListModel {

    public static final TasksListModel DEFAULT = TasksListModel.builder().build();

    @Nullable
    public abstract ImmutableList<Task> tasks();

    public abstract TasksFilterType filter();

    public abstract boolean loading();

    public int findTaskIndexById(String id) {
        ImmutableList<Task> tasks = checkNotNull(tasks());
        int taskIndex = -1;
        for(int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).id().equals(id)) {
                taskIndex = i;
            }
        }

        return taskIndex;

    }

    public Optional<Task> findTaskById(String id) {
        int taskIndex =  findTaskIndexById(id);
        if(taskIndex < 0) return Optional.absent();
        return Optional.of(checkNotNull(tasks()).get(taskIndex));
    }

    public TasksListModel withTasks(ImmutableList<Task> tasks) {
        return toBuilder().tasks(tasks).build();
    }

    public TasksListModel withLoading(boolean loading) {
        return toBuilder().loading(loading).build();
    }

    public TasksListModel withTaskFilter(TasksFilterType filter) {
        return toBuilder().filter(filter).build();
    }

    public TasksListModel withTaskAtIndex(Task task, int index) {
        ImmutableList<Task> tasks = checkNotNull(tasks());
        assertIndexWithBounds(index, tasks);

        ArrayList<Task> copy = new ArrayList<>(tasks);
        copy.set(index, task);
        return withTasks(ImmutableList.copyOf(copy));
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_TasksListModel.Builder().loading(false).filter(TasksFilterType.ALL_TASKS);
    }

    static void assertIndexWithBounds(int index, List<?> items) {
        if(index < 0 || index > items.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
    }

    @AutoValue
    public abstract static class TaskEntry {
        public abstract int index();
        public abstract Task task();

        public static TaskEntry create(int index,  Task task) {
            return new AutoValue_TasksListModel_TaskEntry(index, task);
        }
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder tasks(ImmutableList<Task> tasks);
        public abstract Builder filter(TasksFilterType filter);
        public abstract Builder loading(boolean loading);

        public abstract TasksListModel build();
        
    }


}
