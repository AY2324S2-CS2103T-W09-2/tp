package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandTestUtil;
import seedu.address.logic.commands.EditPersonCommand;
import seedu.address.model.person.Description;
import seedu.address.model.person.PersonEmail;
import seedu.address.model.person.PersonName;

public class EditPersonCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditPersonCommand.MESSAGE_USAGE);

    private EditPersonCommandParser parser = new EditPersonCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, CommandTestUtil.VALID_NAME_A, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1 1", EditPersonCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + CommandTestUtil.NAME_DESC_A, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + CommandTestUtil.NAME_DESC_A, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 x/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1 1"
            + CommandTestUtil.INVALID_PERSONNAME_DESC, PersonName.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1 1"
            + CommandTestUtil.INVALID_PERSONEMAIL_DESC, PersonEmail.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1 1"
            + CommandTestUtil.INVALID_PERSONDESC_DESC, Description.MESSAGE_CONSTRAINTS); // invalid description

        // invalid person name followed by valid email
        assertParseFailure(parser, "1 1"
            + CommandTestUtil.INVALID_PERSONNAME_DESC
            + CommandTestUtil.EMAIL_DESC_A, PersonName.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1 1"
                + CommandTestUtil.INVALID_PERSONNAME_DESC + CommandTestUtil.INVALID_PERSONEMAIL_DESC
                + CommandTestUtil.VALID_DESCRIPTION_CTO,
                PersonName.MESSAGE_CONSTRAINTS);
    }
}
