package com.benmohammad.masmobius.stats;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.benmohammad.masmobius.stats.domain.StatisticsState;
import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

class StatisticsStateBundler {

    static Optional<Bundle> statisticsStateToBundle(StatisticsState state) {
        return state.map(
                loading -> Optional.absent(),
                loaded -> {
                    Bundle b = new Bundle();
                    b.putInt("active_count", loaded.activeCount());
                    b.putInt("completed_count", loaded.completedCount());
                    return Optional.of(b);
                },
                failed -> Optional.absent());
    }

    static StatisticsState bundleToStatisticsState(@Nullable Bundle bundle) {
        if(bundle == null) return StatisticsState.loading();
        if(!bundle.containsKey("statistics")) return StatisticsState.loading();

        bundle = checkNotNull(bundle.getBundle("statistics"));

        if(bundle.containsKey("active_count") && bundle.containsKey("completed_count")) {
            return StatisticsState.loaded(
                    bundle.getInt("activeCount"), bundle.getInt("completed_count"));

        }

        return StatisticsState.loading();
    }
}
