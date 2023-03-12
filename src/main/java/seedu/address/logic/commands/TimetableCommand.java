package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Shows timetable of scheduled jobs
 */
public class TimetableCommand extends Command {
    public static final String COMMAND_WORD = "timetable";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows timetable and job schedule.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_TIMETABLE_MESSAGE = "Opened timetable window.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(SHOWING_TIMETABLE_MESSAGE, true, false);
    }

}
