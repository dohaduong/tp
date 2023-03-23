package seedu.address.logic.commands.jobs;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELIVERY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELIVERY_SLOT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EARNING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IS_DELIVERED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JOB_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RECIPIENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SENDER_ID;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_DELIVERY_JOBS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.jobs.DeliveryDate;
import seedu.address.model.jobs.DeliveryJob;
import seedu.address.model.jobs.DeliverySlot;
import seedu.address.model.jobs.Earning;

/**
 * Edits the details of an existing job in the address book.
 */
public class EditDeliveryJobCommand extends Command {

    public static final String COMMAND_WORD = "edit_job";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the job identified "
            + "by the index number/job id used in the displayed job list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_JOB_ID + "Job Id] "
            + "[" + PREFIX_SENDER_ID + "Sender Id] "
            + "[" + PREFIX_RECIPIENT_ID + "Recipient] "
            + "[" + PREFIX_DELIVERY_DATE + "Date] "
            + "[" + PREFIX_DELIVERY_SLOT + "Slot] "
            + "[" + PREFIX_EARNING + "Earn] "
            + "[" + PREFIX_IS_DELIVERED + "Complete]...\n";

    public static final String MESSAGE_EDIT_JOB_SUCCESS = "Edited Job: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_JOB = "This job already exists in the address book.";

    private final Optional<Index> index;
    private final EditDeliveryJobDescriptor editDeliveryJobDescriptor;

    /**
     * @param index                     of the job in the filtered job list to edit
     * @param editDeliveryJobDescriptor details to edit the job with
     */
    public EditDeliveryJobCommand(Index index, EditDeliveryJobDescriptor editDeliveryJobDescriptor) {
        requireNonNull(index);
        requireNonNull(editDeliveryJobDescriptor);

        this.index = Optional.of(index);
        this.editDeliveryJobDescriptor = new EditDeliveryJobDescriptor(editDeliveryJobDescriptor);
    }

    /**
     * @param editDeliveryJobDescriptor details to edit the job with
     */
    public EditDeliveryJobCommand(EditDeliveryJobDescriptor editDeliveryJobDescriptor) {
        requireNonNull(editDeliveryJobDescriptor);

        this.index = Optional.empty();
        this.editDeliveryJobDescriptor = new EditDeliveryJobDescriptor(editDeliveryJobDescriptor);
    }

    /**
     * Creates and returns a {@code job} with the details of
     * {@code deliveryJobToEdit}
     * edited with {@code editjobDescriptor}.
     */
    private static DeliveryJob createEditedDeliveryJob(DeliveryJob deliveryJobToEdit,
            EditDeliveryJobDescriptor editjobDescriptor) {
        assert deliveryJobToEdit != null;
        DeliveryJob.Builder toEdit = new DeliveryJob.Builder();

        toEdit.jobId(deliveryJobToEdit.getJobId());

        editjobDescriptor.getRecipient().ifPresentOrElse(val -> {
            toEdit.recipient(val);
        }, () -> {
            toEdit.recipient(deliveryJobToEdit.getRecipientId());
        });

        editjobDescriptor.getSender().ifPresentOrElse(val -> {
            toEdit.sender(val);
        }, () -> {
            toEdit.sender(deliveryJobToEdit.getSenderId());
        });

        editjobDescriptor.getDeliveryDate().ifPresentOrElse(val -> {
            toEdit.deliveryDate(val.date);
        }, () -> {
            deliveryJobToEdit.getDeliveryDate().ifPresent(val -> {
                toEdit.deliveryDate(val.date);
            });
        });

        editjobDescriptor.getDeliverySlot().ifPresentOrElse(val -> {
            toEdit.deliverySlot(val.value);
        }, () -> {
            deliveryJobToEdit.getDeliverySlot().ifPresent(val -> {
                toEdit.deliverySlot(val.value);
            });
        });

        editjobDescriptor.getEarning().ifPresentOrElse(val -> {
            toEdit.earning(val.value);
        }, () -> {
            deliveryJobToEdit.getEarning().ifPresent(val -> {
                toEdit.earning(val.value);
            });
        });

        editjobDescriptor.getDelivered().ifPresentOrElse(val -> {
            toEdit.isDelivered(val);
        }, () -> {
            toEdit.isDelivered(deliveryJobToEdit.getDeliveredStatus());
        });

        return toEdit.build();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<DeliveryJob> lastShownList = model.getFilteredDeliveryJobList();

        Optional<DeliveryJob> deliveryJobToEdit;
        if (index.isPresent()) {
            if (index.get().getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            deliveryJobToEdit = Optional.of(lastShownList.get(index.get().getZeroBased()));
        } else {
            if (!editDeliveryJobDescriptor.getJobId().isPresent()) {
                throw new CommandException(Messages.MESSAGE_INVALID_JOB_ID);
            }
            deliveryJobToEdit = lastShownList.stream().filter(x -> x.getJobId().equals(editDeliveryJobDescriptor.jobId))
                    .findAny();
        }

        if (deliveryJobToEdit.isPresent()) {
            DeliveryJob editedDeliveryJob = createEditedDeliveryJob(deliveryJobToEdit.get(), editDeliveryJobDescriptor);

            if (!deliveryJobToEdit.get().isSameDeliveryJob(editedDeliveryJob)
                    && model.hasDeliveryJob(editedDeliveryJob)) {
                throw new CommandException(MESSAGE_DUPLICATE_JOB);
            }

            model.setDeliveryJob(deliveryJobToEdit.get(), editedDeliveryJob);
            model.updateFilteredDeliveryJobList(PREDICATE_SHOW_ALL_DELIVERY_JOBS);
            return new CommandResult(String.format(MESSAGE_EDIT_JOB_SUCCESS, editedDeliveryJob));
        } else {
            throw new CommandException(Messages.MESSAGE_INVALID_JOB_ID);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditDeliveryJobCommand)) {
            return false;
        }

        // state check
        EditDeliveryJobCommand e = (EditDeliveryJobCommand) other;
        return index.equals(e.index)
                && editDeliveryJobDescriptor.equals(e.editDeliveryJobDescriptor);
    }

    /**
     * Stores the details to edit the job with. Each non-empty field value will
     * replace the
     * corresponding field value of the job.
     */
    public static class EditDeliveryJobDescriptor {
        // Identity fields
        private String jobId;

        // Delivery informations
        private String recipient;
        private String sender; // aka customer
        private DeliveryDate deliveryDate;
        private DeliverySlot deliverySlot;
        private Earning earning;
        private boolean isDelivered;

        public EditDeliveryJobDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditDeliveryJobDescriptor(EditDeliveryJobDescriptor toCopy) {
            setJobId(toCopy.jobId);
            setSender(toCopy.sender);
            setRecipient(toCopy.recipient);
            setDeliveryDate(toCopy.deliveryDate);
            setDeliverySlot(toCopy.deliverySlot);
            setEarning(toCopy.earning);
            setDelivered(toCopy.isDelivered);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(jobId, sender, recipient, deliveryDate, deliverySlot, isDelivered);
        }

        public Optional<String> getJobId() {
            return Optional.ofNullable(jobId);
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public Optional<String> getRecipient() {
            return Optional.ofNullable(recipient);
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public Optional<String> getSender() {
            return Optional.ofNullable(sender);
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public Optional<DeliveryDate> getDeliveryDate() {
            return Optional.ofNullable(deliveryDate);
        }

        public void setDeliveryDate(DeliveryDate deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public Optional<DeliverySlot> getDeliverySlot() {
            return Optional.ofNullable(deliverySlot);
        }

        public void setDeliverySlot(DeliverySlot deliverySlot) {
            this.deliverySlot = deliverySlot;
        }

        public Optional<Earning> getEarning() {
            return Optional.ofNullable(earning);
        }

        public void setEarning(Earning earning) {
            this.earning = earning;
        }

        public Optional<Boolean> getDelivered() {
            return Optional.ofNullable(isDelivered);
        }

        public void setDelivered(boolean isDelivered) {
            this.isDelivered = isDelivered;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditDeliveryJobDescriptor)) {
                return false;
            }

            // state check
            EditDeliveryJobDescriptor e = (EditDeliveryJobDescriptor) other;

            return getJobId().equals(e.getJobId())
                    && getSender().equals(e.getSender())
                    && getRecipient().equals(e.getRecipient())
                    && getDeliveryDate().equals(e.getDeliveryDate())
                    && getDeliverySlot().equals(e.getDeliverySlot())
                    && getEarning().equals(e.getEarning())
                    && getDelivered().equals(e.getDelivered());
        }
    }
}