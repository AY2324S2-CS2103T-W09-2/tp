package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditStartupDescriptor;
import seedu.address.testutil.EditStartupDescriptorBuilder;

public class EditStartupDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditStartupDescriptor descriptorWithSameValues = new EditStartupDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditStartupDescriptor editedAmy = new EditStartupDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditStartupDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditStartupDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditStartupDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditStartupDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditStartupDescriptor editStartupDescriptor = new EditStartupDescriptor();
        String expected = EditStartupDescriptor.class.getCanonicalName() + "{name="
                + editStartupDescriptor.getName().orElse(null) + ", industry="
                + editStartupDescriptor.getIndustry().orElse(null) + ", funding stage="
                + editStartupDescriptor.getFundingStage().orElse(null) + ", phone="
                + editStartupDescriptor.getPhone().orElse(null) + ", email="
                + editStartupDescriptor.getEmail().orElse(null) + ", address="
                + editStartupDescriptor.getAddress().orElse(null) + ", valuation="
                + editStartupDescriptor.getValuation().orElse(null) + ", tags="
                + editStartupDescriptor.getTags().orElse(null) + "}";
        assertEquals(expected, editStartupDescriptor.toString());
    }
}
