package com.benmohammad.masmobius.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.task.domain.TaskFilters;
import com.benmohammad.masmobius.task.domain.TasksFilterType;
import com.benmohammad.masmobius.task.domain.TasksListEvent;
import com.benmohammad.masmobius.task.domain.TasksListModel;
import com.benmohammad.masmobius.task.effecthanlders.TasksListEffectHandlers;
import com.benmohammad.masmobius.task.view.DeferredEventSource;
import com.benmohammad.masmobius.task.view.TasksListViewDataMapper;
import com.benmohammad.masmobius.task.view.TasksViews;
import com.benmohammad.masmobius.taskdetail.TaskDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.mobius.MobiusLoop;

import io.reactivex.subjects.PublishSubject;

import static com.benmohammad.masmobius.task.domain.TasksListEvent.clearCompletedTasksRequested;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.filterSelected;
import static com.benmohammad.masmobius.task.domain.TasksListEvent.refreshRequested;
import static com.benmohammad.masmobius.task.domain.TasksListModelBundlePacker.tasksListModelFromBundle;
import static com.benmohammad.masmobius.task.domain.TasksListModelBundlePacker.tasksListModelToBundle;
import static com.benmohammad.masmobius.task.effecthanlders.TasksListEffectHandlers.createEffectHandler;
import static com.spotify.mobius.extras.Connectables.contramap;

public class TaskFragment extends Fragment {

    private MobiusLoop.Controller<TasksListModel, TasksListEvent> mController;
    private PublishSubject<TasksListEvent> mMenuEvents = PublishSubject.create();

    private TasksViews mViews;
    private DeferredEventSource<TasksListEvent> mEventSource = new DeferredEventSource<>();

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_task);
        mViews= new TasksViews(inflater, container, fab, mMenuEvents);

        mController = TasksInjector.createController(
                createEffectHandler(getContext(), mViews, this::showAddTask, this::showTaskDetailUi),
                mEventSource,
                resolveDefaultModel(savedInstanceState));

        mController.connect(contramap(TasksListViewDataMapper::tasksListModelToViewData, mViews));
        setHasOptionsMenu(true);
        return mViews.getRootView();
    }

    private TasksListModel resolveDefaultModel(@Nullable Bundle savedInstanceState) {
        return savedInstanceState != null ? tasksListModelFromBundle(savedInstanceState.getBundle("model")) : TasksListModel.DEFAULT;
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.start();
    }

    @Override
    public void onPause() {
        mController.stop();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", tasksListModelToBundle(mController.getModel()));
    }

    @Override
    public void onDestroyView() {
        mController.disconnect();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.task_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mMenuEvents.onNext(clearCompletedTasksRequested());
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mMenuEvents.onNext(refreshRequested());
                break;
        }
        return true;
    }

    private void onFilteredSelected(TasksFilterType filter) {
        mMenuEvents.onNext(filterSelected(filter));
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popupMenu.getMenuInflater().inflate(R.menu.filter_tasks, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(
                item -> {
                    switch(item.getItemId()) {
                        case R.id.active:
                            onFilteredSelected(TasksFilterType.ACTIVE_TASKS);
                            break;
                        case R.id.completed:
                            onFilteredSelected(TasksFilterType.COMPLETED_TASKS);
                            break;
                        default:
                            onFilteredSelected(TasksFilterType.ALL_TASKS);
                            break;
                    }

                    return true;
                });
        popupMenu.show();
    }

    public void showAddTask() {
        //startActivityForResult(AddEditTaskActivity.addTask(getContext()), AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    public void showTaskDetailUi(Task task) {
        startActivity(TaskDetailActivity.showTask(getContext(), task));
    }

}
