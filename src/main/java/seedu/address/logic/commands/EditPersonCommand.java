package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.model.Model;
import seedu.address.model.person.Description;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonEmail;
import seedu.address.model.person.PersonName;
import seedu.address.model.startup.Startup;


/**
 * Edits the details of an existing person in the address book.
 */
public class EditPersonCommand extends Command {

    public static final String COMMAND_WORD = "edit-p";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "PERSON_INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_PERSON_NAME + "NAME] "
            + "[" + CliSyntax.PREFIX_PERSON_EMAIL + "EMAIL] "
            + "[" + CliSyntax.PREFIX_PERSON_DESCRIPTION + "DESCRIPTION]...\n"
            + "Example: " + COMMAND_WORD + " 1 1 "
            + CliSyntax.PREFIX_PERSON_NAME + "John Doe "
            + CliSyntax.PREFIX_PERSON_EMAIL + "johndoe@example.com "
            + CliSyntax.PREFIX_PERSON_DESCRIPTION + "founder";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final int personIndex;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Creates an EditPersonCommand with the specified input
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditPersonCommand(Index index, int personIndex, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.personIndex = personIndex - 1;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Startup> lastShownList = model.getFilteredStartupList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
        }

        Startup startupToEdit = lastShownList.get(index.getZeroBased());
        if (personIndex >= startupToEdit.getPersons().size()) {
            throw new CommandException("The person index provided is invalid.");
        }

        Person personToEdit = startupToEdit.getPersons().get(personIndex);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
        if (!personToEdit.isSamePerson(editedPerson) && startupToEdit.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        ArrayList<Person> updatedPersons = new ArrayList<>(startupToEdit.getPersons());
        updatedPersons.set(personIndex, editedPerson);
        Startup editedStartup = new Startup(
                startupToEdit.getName(),
                startupToEdit.getFundingStage(),
                startupToEdit.getIndustry(),
                startupToEdit.getPhone(),
                startupToEdit.getEmail(),
                startupToEdit.getAddress(),
                startupToEdit.getValuation(),
                startupToEdit.getTags(),
                startupToEdit.getNotes(),
                updatedPersons
        );

        model.setStartup(startupToEdit, editedStartup);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        PersonName updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        PersonEmail updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Set<Description> updatedDescriptions = editPersonDescriptor.getDescriptions()
                        .orElse(personToEdit.getDescriptions());
        return new Person(updatedName, updatedEmail, updatedDescriptions);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditPersonCommand)) {
            return false;
        }

        EditPersonCommand otherEditCommand = (EditPersonCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private PersonName name;
        private PersonEmail email;
        private Set<Description> descriptions;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code EditPersonDescriptor} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setEmail(toCopy.email);
            setDescriptions(toCopy.descriptions);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, email, descriptions);
        }

        public void setName(PersonName name) {
            this.name = name;
        }

        public Optional<PersonName> getName() {
            return Optional.ofNullable(name);
        }

        public void setEmail(PersonEmail email) {
            this.email = email;
        }

        public Optional<PersonEmail> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets {@code descriptions} to this object's {@code descriptions}.
         * A defensive copy of {@code descriptions} is used internally.
         */
        public void setDescriptions(Set<Description> descriptions) {
            this.descriptions = (descriptions != null) ? new HashSet<>(descriptions) : null;
        }

        /**
         * Returns an unmodifiable description set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code descriptions} is null.
         */
        public Optional<Set<Description>> getDescriptions() {
            return (descriptions != null) ? Optional.of(Collections.unmodifiableSet(descriptions)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(descriptions, otherEditPersonDescriptor.descriptions);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("email", email)
                    .add("descriptions", descriptions)
                    .toString();
        }
    }
}
