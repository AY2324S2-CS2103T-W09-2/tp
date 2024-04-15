package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.startup.Startup;

/**
 * Delete a Person of a startup in the address book!
 */
public class DeletePersonCommand extends Command {
    public static final String COMMAND_WORD = "delete-p";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a key employee from the startup identified "
            + "by the index number used in the displayed startup list. "
            + "Parameters: INDEX (must be a positive integer) personIndex (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 1";

    public static final String MESSAGE_SUCCESS = "Employee deleted from Startup: %1$s";

    private final Index index;
    private final int personIndex;

    /**
     * Creates a DeletePersonCommand with the specified inputs.
     * @param index Index of the startup
     * @param personIndex Index of the person within the startup
     */
    public DeletePersonCommand(Index index, int personIndex) {
        requireNonNull(index);
        this.index = index;
        this.personIndex = personIndex - 1; // User input is 1-based, internal storage is 0-based.
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

        List<Person> updatedPersons = new ArrayList<>(startupToEdit.getPersons());
        updatedPersons.remove(personIndex);
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
        if (this == other) { // same object instance
            return true;
        }

        if (!(other instanceof DeletePersonCommand)) { // not the same type
            return false;
        }

        DeletePersonCommand e = (DeletePersonCommand) other;
        // Compare both the startup index and the person index within that startup
        return index.equals(e.index)
                && personIndex == e.personIndex;
    }

}


