package com.benmohammad.masmobius.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.masmobius.stats.domain.StatisticsEvent;
import com.benmohammad.masmobius.stats.domain.StatisticsState;
import com.benmohammad.masmobius.stats.view.StatisticsViews;
import com.google.common.base.Optional;
import com.spotify.mobius.MobiusLoop;

import static com.benmohammad.masmobius.stats.StatisticsInjector.createController;
import static com.benmohammad.masmobius.stats.StatisticsStateBundler.bundleToStatisticsState;
import static com.benmohammad.masmobius.stats.StatisticsStateBundler.statisticsStateToBundle;
import static com.benmohammad.masmobius.stats.effecthandlers.StatisticsEffectHandlers.createEffectHandler;
import static com.google.common.base.Preconditions.checkNotNull;

public class StatisticsFragment extends Fragment {

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    private MobiusLoop.Controller<StatisticsState, StatisticsEvent> mController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatisticsViews views = new StatisticsViews(inflater, checkNotNull(container));
        mController = createController(createEffectHandler(getContext()), bundleToStatisticsState(savedInstanceState));
        mController.connect(views);
        return views.getRootView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Optional<Bundle> optionalBundle = statisticsStateToBundle(mController.getModel());
        if(optionalBundle.isPresent()) {
            outState.putBundle("statistics", optionalBundle.get());
        }
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
    public void onDestroyView() {
        mController.disconnect();
        super.onDestroyView();
    }
}
