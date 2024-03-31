package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;


import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.startup.Note;
import seedu.address.model.startup.Startup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adds a startup to the address book.
 */
public class AddPersonCommand extends Command {

    public static final String COMMAND_WORD = "add-p";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the startup "
            + "key persons section in CapitalConnect. "
            + "Parameters: "
            + CliSyntax.PREFIX_PERSON_NAME + "NAME "
            + CliSyntax.PREFIX_EMAIL + "EMAIL  "
            + CliSyntax.PREFIX_PERSON_DESCRIPTION + "DESCRIPTION "
            + "Example: " + COMMAND_WORD
            + CliSyntax.PREFIX_PERSON_NAME + "John "
            + CliSyntax.PREFIX_PERSON_EMAIL + "johndoe@example.com "
            + CliSyntax.PREFIX_PERSON_DESCRIPTION + "founder";


    public static final String MESSAGE_SUCCESS = "New person added to startup: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in CapitalConnect";

    private final Index index;
    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddPersonCommand(Index index, Person person) {
        requireNonNull(person);
        requireNonNull(index);
        this.index = index;
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Startup> lastShownList = model.getFilteredStartupList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
        }

        Startup startupToEdit = lastShownList.get(index.getZeroBased());

        // Creates a new Set from the existing persons and adds the new person
        Set<Person> updatedPersons = new HashSet<>(startupToEdit.getPersons());
        updatedPersons.add(toAdd);

        // Now directly use the updated constructor
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
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedStartup));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddPersonCommand)) {
            return false;
        }

        AddPersonCommand otherAddCommand = (AddPersonCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}