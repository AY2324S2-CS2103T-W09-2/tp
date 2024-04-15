package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DESCRIPTION_FOUNDER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_B;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStartupAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STARTUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STARTUP;
import static seedu.address.testutil.TypicalStartups.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditPersonCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.startup.Startup;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.StartupBuilder;
import seedu.address.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditPersonCommand.
 */
public class EditPersonCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Person editedPerson = new PersonBuilder()
                        .withName("Carl").withEmail("carl@gmail.com")
                        .withDescriptions("new description").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(INDEX_FIRST_STARTUP, 1, descriptor);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit).withPersons(editedPerson).build();

        String expectedMessage = String.format(
                EditPersonCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        expectedModel.setStartup(startupToEdit, expectedStartup);

        assertCommandSuccess(editPersonCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastStartup = Index.fromOneBased(model.getFilteredStartupList().size());
        Startup startupToEdit = model.getFilteredStartupList().get(indexLastStartup.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(startupToEdit.getPersons().get(1));
        Person editedPerson = personInList.withName(VALID_NAME_B)
                .withDescriptions(VALID_DESCRIPTION_FOUNDER).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_B)
                .withDescriptions(VALID_DESCRIPTION_FOUNDER).build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(indexLastStartup, 2, descriptor);

        String expectedMessage = String.format(
                EditPersonCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Startup expectedStartup = new StartupBuilder(startupToEdit)
                .withPersons(TypicalPersons.AMY, editedPerson).build();

        expectedModel.setStartup(startupToEdit, expectedStartup);

        assertCommandSuccess(editPersonCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showStartupAtIndex(model, INDEX_FIRST_STARTUP);

        Startup startupInFilteredList = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        Startup editedStartup = new StartupBuilder(startupInFilteredList).withPersons(TypicalPersons.CARL).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(TypicalPersons.CARL).build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(INDEX_FIRST_STARTUP, 1, descriptor);

        String expectedMessage = String.format(
                EditPersonCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(TypicalPersons.CARL));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStartup(model.getFilteredStartupList().get(0), editedStartup);
        showStartupAtIndex(expectedModel, INDEX_FIRST_STARTUP);

        assertCommandSuccess(editPersonCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartupIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStartupList().size() + 1);
        Person editedPerson = new PersonBuilder()
                .withName("Carl").withEmail("carl@gmail.com")
                .withDescriptions("new description").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(outOfBoundIndex, 1, descriptor);

        assertCommandFailure(editPersonCommand, model, Messages.MESSAGE_INVALID_STARTUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Startup startupToEdit = model.getFilteredStartupList().get(INDEX_FIRST_STARTUP.getZeroBased());
        int outOfBoundIndex = startupToEdit.getPersons().size() + 1;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Carl").build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(INDEX_FIRST_STARTUP, outOfBoundIndex, descriptor);

        assertCommandFailure(editPersonCommand, model, "The person index provided is invalid.");
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Index indexLastStartup = Index.fromOneBased(model.getFilteredStartupList().size());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail("george@example.com").build();
        EditPersonCommand editPersonCommand = new EditPersonCommand(indexLastStartup, 1, descriptor);

        assertCommandFailure(editPersonCommand, model, EditPersonCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void equals() {
        final EditPersonCommand standardCommand =
                new EditPersonCommand(INDEX_FIRST_STARTUP,
                        0, new EditPersonDescriptorBuilder(TypicalPersons.AMY).build());

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptorBuilder(TypicalPersons.AMY).build();
        EditPersonCommand commandWithSameValues = new EditPersonCommand(INDEX_FIRST_STARTUP, 0, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditPersonCommand(
                INDEX_SECOND_STARTUP, 0,
                new EditPersonDescriptorBuilder(TypicalPersons.AMY).build())));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditPersonCommand(
                INDEX_FIRST_STARTUP, 0,
                new EditPersonDescriptorBuilder(TypicalPersons.BOB).build())));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditPersonCommand editPersonCommand = new EditPersonCommand(index, 0, editPersonDescriptor);
        String expected = EditPersonCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editPersonCommand.toString());
    }

}
