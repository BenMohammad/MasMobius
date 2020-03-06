package com.benmohammad.masmobius.addedittask.view;

import android.os.Bundle;

import com.benmohammad.masmobius.addedittask.domain.AddEditTaskMode;
import com.benmohammad.masmobius.addedittask.domain.AddEditTaskModel;
import com.benmohammad.masmobius.data.TaskBundlePacker;
import com.benmohammad.masmobius.data.TaskDetails;
import com.google.common.base.Optional;

import static com.benmohammad.masmobius.data.TaskBundlePacker.taskDetailsFromBundle;
import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditTaskModeBundlePacker {

    public static Bundle addEditTaskModelToBundle(AddEditTaskModel model) {
        Bundle b = new Bundle();
        b.putBundle("task_details", TaskBundlePacker.taskDetailsToBundle(model.details()));
        Optional<Bundle> modeBundle = addEditModeToBundle(model.mode());
        if(modeBundle.isPresent()) b.putBundle("add_edit_mode", modeBundle.get());
        return b;
    }

    public static AddEditTaskModel addEditTaskModelFromBundle(Bundle bundle) {
        return AddEditTaskModel.builder()
                .details(taskDetailsFromBundle(checkNotNull(bundle.getBundle("task_details"))))
                .mode(addEditTaskModeFromBundle(bundle.getBundle("add_edit_mode")))
                .build();
    }

    public static Optional<Bundle> addEditModeToBundle(AddEditTaskMode mode) {
        return mode.map(
                create -> Optional.absent(),
                update -> {
                    Bundle b = new Bundle();
                    b.putString("task_id", update.id());
                    return Optional.of(b);
                }
        );
    }

    public static AddEditTaskMode addEditTaskModeFromBundle(Bundle bundle) {
        if(bundle == null) return AddEditTaskMode.create();
        return AddEditTaskMode.update(checkNotNull(bundle.getString("task_id")));
    }
}
