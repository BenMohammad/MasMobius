package com.benmohammad.masmobius.stats.domain;

import com.benmohammad.masmobius.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface StatisticsEvent_dataenum {

    dataenum_case TasksLoaded(ImmutableList<Task> tasks);
    dataenum_case TaskLoadingFailed();
}
