package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;



/**
 * Tests that a {@code Startup}'s {@code Industry} matches any of the keywords given.
 */
public class IndustryContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public IndustryContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getIndustry().value, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof IndustryContainsKeywordsPredicate)) {
            return false;
        }

        IndustryContainsKeywordsPredicate otherIndustryContainsKeywordsPredicate =
                (IndustryContainsKeywordsPredicate) other;
        return keywords.equals(otherIndustryContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
