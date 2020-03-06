package com.benmohammad.masmobius.stats.view;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benmohammad.masmobius.R;
import com.benmohammad.masmobius.stats.domain.StatisticsEvent;
import com.benmohammad.masmobius.stats.domain.StatisticsState;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

public class StatisticsViews implements Connectable<StatisticsState, StatisticsEvent> {

    private final View mRoot;
    private final TextView mStatisticsTV;

    public StatisticsViews(LayoutInflater inflater, ViewGroup parent) {
        mRoot = inflater.inflate(R.layout.statistics_frag, parent, false);
        mStatisticsTV = mRoot.findViewById(R.id.statistics);
    }

    public View getRootView() {
        return mRoot;
    }

    @Nonnull
    @Override
    public Connection<StatisticsState> connect(Consumer<StatisticsEvent> output) throws ConnectionLimitExceededException {
        return new Connection<StatisticsState>() {
            @Override
            public void accept(StatisticsState value) {
                renderState(value);
            }

            @Override
            public void dispose() {

            }
        };
    }

    private void renderState(StatisticsState state) {
        state.match(
                loading -> mStatisticsTV.setText(R.string.loading),
                loaded -> {
                    int numOfCompletedTasks =  loaded.completedCount();
                    int numOfIncompleteTasks = loaded.activeCount();
                    if(numOfCompletedTasks == 0 && numOfIncompleteTasks == 0) {
                        mStatisticsTV.setText(R.string.statistics_no_tasks);
                    } else {
                        Resources resources = mRoot.getContext().getResources();
                        String activeTaskString = resources.getString(R.string.statistics_active_tasks);

                        String completedTaskString =
                                resources.getString(R.string.statistics_completed_tasks);

                        String displayString = activeTaskString + "\n" + completedTaskString;
                        mStatisticsTV.setText(displayString);
                    }
                },
                failed -> mStatisticsTV.setText(R.string.statistics_error)
        );
    }
}
