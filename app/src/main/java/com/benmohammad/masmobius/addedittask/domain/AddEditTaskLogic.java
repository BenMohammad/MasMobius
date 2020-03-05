package com.benmohammad.masmobius.addedittask.domain;

import androidx.annotation.NonNull;

import com.benmohammad.masmobius.data.Task;
import com.benmohammad.masmobius.data.TaskDetails;
import com.spotify.mobius.Next;

import static com.benmohammad.masmobius.addedittask.domain.AddEditTaskEffect.createTask;
import static com.benmohammad.masmobius.addedittask.domain.AddEditTaskEffect.exit;
import static com.benmohammad.masmobius.addedittask.domain.AddEditTaskEffect.notifyEmptyTaskNotAllowed;
import static com.benmohammad.masmobius.addedittask.domain.AddEditTaskEffect.saveTask;
import static com.benmohammad.masmobius.addedittask.domain.AddEditTaskEvent.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.spotify.mobius.Effects.effects;
import static com.spotify.mobius.Next.dispatch;
import static com.spotify.mobius.Next.next;

public class AddEditTaskLogic {

    @NonNull
    public static Next<AddEditTaskModel, AddEditTaskEffect> update(
            AddEditTaskModel model, AddEditTaskEvent event
    ) {
        return event.map(
                taskDefinitionCompleted -> onTaskDefinitionCompleted(model, taskDefinitionCompleted),
                taskCreatedSuccessfully -> exitWithSuccess(),
                taskCreationFailed -> exitWithFailure(),
                taskUpdatedSuccessfully -> exitWithSuccess(),
                taskUpdateFailed -> exitWithFailure()
        );
    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> onTaskDefinitionCompleted(
            AddEditTaskModel model, TaskDefinitionCompleted definitionCompleted
    ) {
        String title =definitionCompleted.title().trim();
        String description = definitionCompleted.description().trim();

        if(isNullOrEmpty(title) && isNullOrEmpty(description)) {
            return Next.dispatch(effects(notifyEmptyTaskNotAllowed()));
        }

        TaskDetails details = model.details().toBuilder().title(title).description(description).build();
        AddEditTaskModel newModel = model.withDetails(details);

        return newModel
                .mode()
                .map(
                        create -> next(newModel, effects(createTask(newModel.details()))),
                        update -> next(newModel, effects(saveTask(Task.create(update.id(), newModel.details())))));
    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> exitWithSuccess() {
        return dispatch(effects(exit(true)));
    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> exitWithFailure() {
        return dispatch(effects(exit(false)));
    }

}
