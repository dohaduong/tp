package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.ui.NotificationManager;

/**
 * Lists all reminders as Notifications to the user.
 */
public class ListReminder extends Command {

    public static final String COMMAND_WORD = "list_reminder";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all reminders.\n";

    public static final String MESSAGE_SUCCESS = "Listed all reminders";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.getReminderList();
        NotificationManager notification = new NotificationManager(model);
        notification.listReminders();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
