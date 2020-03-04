package com.benmohammad.masmobius.task.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.task.domain.TasksListEvent;
import com.benmohammad.masmobius.task.view.TasksListViewData.TaskViewData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.navigateToTaskDetailsRequested;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.newTaskClicked;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.refreshRequested;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.taskMarkedActive;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.taskMarkedComplete;

public class TasksViews implements TasksListViewActions, Connectable<TasksListViewData, TasksListEvent> {

    private final View mRoot;
    private final ScrollChildSwipeRefreshLayout swipeRefreshLayout;
    private final FloatingActionButton mFab;
    private final Observable<TasksListEvent> menuEvents;

    private TasksAdapter mListAdapter;
    private View mNoTaskView;
    private ImageView mNoTaskIcon;
    private TextView mNoTaskMainView;
    private TextView mNoTaskAddView;
    private LinearLayout mTasksView;
    private TextView mFilteringLabelView;

    public TasksViews(LayoutInflater inflater,
                      ViewGroup parent,
                      FloatingActionButton fab,
                      Observable<TasksListEvent> menuEvents) {
        this.menuEvents = menuEvents;
        mRoot = inflater.inflate(R.layout.tasks_frag, parent, false);
        mListAdapter = new TasksAdapter();
        ListView listView =mRoot.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = mRoot.findViewById(R.id.filteringLabel);
        mTasksView = mRoot.findViewById(R.id.tasksLL);

        mNoTaskView = mRoot.findViewById(R.id.noTasks);
        mNoTaskIcon = mRoot.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = mRoot.findViewById(R.id.noTasksMain);
        mNoTaskAddView = mRoot.findViewById(R.id.noTasksAdd);
        fab.setImageResource(R.drawable.ic_add);
        mFab = fab;
        swipeRefreshLayout = mRoot.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(mRoot.getContext(), R.color.colorPrimary),
                ContextCompat.getColor(mRoot.getContext(), R.color.colorAccent),
                ContextCompat.getColor(mRoot.getContext(), R.color.colorPrimaryDark));

        swipeRefreshLayout.setScrollUpChild(listView);
    }


    public View getRootView() {
        return mRoot;
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage(R.string.task_marked_complete);
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(R.string.task_marked_active);
    }

    @Override
    public void showCompleteTaskCleared() {
        showMessage(R.string.completed_tasks_cleared);
    }

    @Override
    public void showLoadingTaskError() {
        showMessage(R.string.loading_tasks_error);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(R.string.successfully_saved_task_message);
    }

    private void showMessage(int messageRes) {
        Snackbar.make(mRoot, messageRes, Snackbar.LENGTH_SHORT).show();
    }

    @Nonnull
    @Override
    public Connection<TasksListViewData> connect(Consumer<TasksListEvent> output) throws ConnectionLimitExceededException {
        addUiListener(output);
        Disposable disposable = menuEvents.subscribe(output::accept);

        return new Connection<TasksListViewData>() {
            @Override
            public void accept(TasksListViewData value) {
                render(value);
            }

            @Override
            public void dispose() {
                disposable.dispose();
                mNoTaskAddView.setOnClickListener(null);
                mFab.setOnClickListener(null);
                swipeRefreshLayout.setOnRefreshListener(null);
                mListAdapter.setTaskItemListener(null);
            }
        };
    }

    private void render(TasksListViewData value) {
        swipeRefreshLayout.setRefreshing(value.loading());
        mFilteringLabelView.setText(value.filterLabel());
        value.viewState()
                .match(
                        awaiting -> showNoTasksViewState(),
                        emptyTasks -> showEmptyTasksState(emptyTasks.viewData()),
                        hasTasks -> showTasks(hasTasks.taskViewData()));
    }

    private void addUiListener(Consumer<TasksListEvent> output) {
        mNoTaskAddView.setOnClickListener(v -> output.accept(newTaskClicked()));
        mFab.setOnClickListener(v -> output.accept(newTaskClicked()));
        swipeRefreshLayout.setOnRefreshListener(() -> output.accept(refreshRequested()));
        mListAdapter.setTaskItemListener(
                new TasksAdapter.TaskItemListener() {
                    @Override
                    public void onTaskClicked(String id) {
                        output.accept(navigateToTaskDetailsRequested(id));
                    }

                    @Override
                    public void onCompleteTaskClick(String id) {
                        output.accept(taskMarkedComplete(id));
                    }

                    @Override
                    public void onActiveTaskClick(String id) {
                        output.accept(taskMarkedActive(id));
                    }
                }
        );
    }

    private void showEmptyTasksState(TasksListViewData.EmptyTaskViewData vd) {
        mTasksView.setVisibility(GONE);
        mNoTaskView.setVisibility(GONE);
        mNoTaskMainView.setText(vd.title());
        mNoTaskIcon.setImageResource(vd.icon());
        mNoTaskAddView.setVisibility(vd.addViewVisibility());
    }

    private void showNoTasksViewState() {
        mTasksView.setVisibility(GONE);
        mNoTaskView.setVisibility(GONE);
    }

    private void showTasks(ImmutableList<TaskViewData> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTaskView.setVisibility(GONE);
    }

}
