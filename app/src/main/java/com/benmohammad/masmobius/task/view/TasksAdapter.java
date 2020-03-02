package com.benmohammad.masmobius.task.view;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.task.view.TasksListViewData.TaskViewData;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksAdapter extends BaseAdapter {

    private ImmutableList<TaskViewData> mTasks;
    private TaskItemListener taskItemListener;

    public void setTaskItemListener(TaskItemListener listener) {
        this.taskItemListener = listener;
    }

    public void replaceData(ImmutableList<TaskViewData> tasks) {
        mTasks = checkNotNull(tasks);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTasks == null ? 0 : mTasks.size();
    }

    @Override
    public TaskViewData getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.task_item, parent, false);
        }

        final TaskViewData task = getItem(position);
        TextView titleTv = rowView.findViewById(R.id.title);
        titleTv.setText(task.title());

        CheckBox completeCB = rowView.findViewById(R.id.complete);
        completeCB.setChecked(task.completed());

        Drawable backGround = parent.getContext().getResources().getDrawable(task.backgroundDrawableId());
        rowView.setBackgroundDrawable(backGround);

        completeCB.setOnClickListener( v -> {
            if (taskItemListener == null) return;
            if (!task.completed()) {
                taskItemListener.onCompleteTaskClick(task.id());
            } else {
                taskItemListener.onActiveTaskClick(task.id());
            }

        });
        rowView.setOnClickListener(

                v -> {
                    if (taskItemListener != null) taskItemListener.onTaskClicked(task.id());


                });

        return rowView;

    }
    public interface TaskItemListener {
        void onTaskClicked(String id);
        void onCompleteTaskClick(String id);
        void onActiveTaskClick(String id);
    }

}
