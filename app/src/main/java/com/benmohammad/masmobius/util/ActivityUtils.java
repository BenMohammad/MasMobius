package com.benmohammad.masmobius.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

public class ActivityUtils {

    public static void addFragmentToActivity(@NonNull FragmentManager manager, @NonNull Fragment fragment, int frameId) {
        checkNotNull(manager);
        checkNotNull(fragment);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
