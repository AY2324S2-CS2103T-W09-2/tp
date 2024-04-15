package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditPersonCommand;
import seedu.address.logic.commands.EditPersonCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Description;



/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditPersonCommandParser implements Parser<EditPersonCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditPersonCommand
     * and returns an EditPersonCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditPersonCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] indexParts = args.trim().split("\\s+");
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_PERSON_NAME,
                CliSyntax.PREFIX_PERSON_EMAIL, CliSyntax.PREFIX_PERSON_DESCRIPTION);

        if (!indexParts[0].matches("\\d+") || !indexParts[1].matches("\\d+")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditPersonCommand.MESSAGE_USAGE));
        }

        Index startupIndex;
        Index personIndex;

        try {
            startupIndex = ParserUtil.parseIndex(indexParts[0]);
            personIndex = ParserUtil.parseIndex(indexParts[1]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditPersonCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_PERSON_NAME, CliSyntax.PREFIX_PERSON_EMAIL);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(CliSyntax.PREFIX_PERSON_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil
                    .parsePersonName(argMultimap.getValue(CliSyntax.PREFIX_PERSON_NAME).get()));
        }
        if (argMultimap.getValue(CliSyntax.PREFIX_PERSON_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil
                    .parsePersonEmail(argMultimap.getValue(CliSyntax.PREFIX_PERSON_EMAIL).get()));
        }

        parseDescriptionsForEdit(argMultimap
                .getAllValues(CliSyntax.PREFIX_PERSON_DESCRIPTION))
                .ifPresent(editPersonDescriptor::setDescriptions);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditPersonCommand(startupIndex, personIndex.getOneBased(), editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Description>> parseDescriptionsForEdit(Collection<String> descriptions)
            throws ParseException {
        assert descriptions != null;

        if (descriptions.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> descriptionSet = descriptions.size() == 1 && descriptions.contains("")
                ? Collections.emptySet()
                : descriptions;
        return Optional.of(ParserUtil.parseDescriptions(descriptionSet));
    }
}
