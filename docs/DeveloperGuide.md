---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CapitalConnect Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**
* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `StartupListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Startup` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a startup).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Startup` objects (which are contained in a `UniqueStartupList` object).
* stores the currently 'selected' `Startup` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Startup>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Startup` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Startup` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Find Commands
The find commands allows users to find the startups with matching names, funding stages, or industries.
There are three find commands in CapitalConnect: `find n/`, `find i/`, `find f`. The implementation of these three commands are similar. Here we use the `find n/` command to illustrate how they are executed.

#### Find by Name
The `find n/` function allows users to find the startups that contain the names that users are interested in.

This function will display startup cards that have the same name as users provide. The startup card contains information other than the startup names, such as addresses, emails, phone numbers, etc..

To find the wanted startup, a `NameContainsKeywordsPredicate` is created to test whether a startup has a name that matches the user's input keywords. Similarly, `find f/` and `find i/` will create `FundingStageContainsKeywordsPredicate` and `InudstryContainsKeywordsPredicate` respectively. These commands only change the displayed list of startups, stored as `filteredStartups` in `Model`, without affecting the data stored in CapitalConnect.

A typical program flow is as follows:

1. User enters a command to find startups by names, e.g. `find n/Apple`.
2. The input is passed to the `AddressbookParser` class which calls `FindCommandParser`. `FindCommandParser` attempts to parse the flags present, and in this case is `n/`. Note that `FindCommandParser` does not check invalid inputs like partial keywords. `FindCommandParser` will only throw an exception if the keyword is empty.
3. If the parse is successful, a `NameContainsKeywordsPredicate` is created to find the startups that contain the name `Apple`.
4. A new `FindCommand` is created from the predicate and passed back to `LogicManager`.
5. The command is executed. The `filteredStartups` is updated with the predicate passed into the command.
6. The command result is created and passed back to the `LogicManager`

The following sequence diagram illustrates the execution process of a find by name command.

<puml src="diagrams/FindByName.puml" alt="FindByName" />

### AddNote, EditNote, and DeleteNote Commands

The `addnote`, `editnote`, and `deletenote` commands enhance the ability of users to manage detailed annotations for each startup in CapitalConnect. These commands allow users to append notes, modify existing ones, or remove them from startups respectively. This section will delve into the implementations of these commands.

#### AddNote Command

The `addnote` command allows users to add a note to a specific startup.

**Program Flow:**
1. **Input Parsing**: The user enters a command like `addnote 1 This is a new note.` The input is processed by `AddNoteCommandParser` which parses the startup index and the note content.
2. **Command Execution**: A new `Note` object is created, and `AddNoteCommand` is instantiated with the parsed index and `Note`. When executed, the command retrieves the specified startup by index from `Model`, adds the new note to the startup's notes list, and updates the model.
3. **Result Generation**: A success message is generated, indicating that the note has been successfully added.

**Sequence Diagram for AddNote Command**:

<puml src="diagrams/AddNoteSequenceDiagram.puml" alt="AddNote" />

#### EditNote Command

The `editnote` command enables the modification of an existing note within a startup's note list.

**Program Flow:**
1. **Input Parsing**: Users input a command like `editnote 1 2 Updated content of the note.` where `1` is the startup index and `2` is the note index within that startup. `EditNoteCommandParser` parses these indices and the new note content.
2. **Command Execution**: The command looks up the startup by the first index and the note by the second index, then replaces the old note content with the new one.
3. **Result Generation**: A success message is displayed to indicate the note was edited.

**Sequence Diagram for EditNote Command**:

<puml src="diagrams/EditNoteSequenceDiagram.puml" alt="EditNote" />

#### DeleteNote Command

The `deletenote` command allows users to remove a note from a startup.

**Program Flow:**
1. **Input Parsing**: The user enters a command like `deletenote 1 1`, aiming to delete the first note of the first startup. `DeleteNoteCommandParser` parses the indices.
2. **Command Execution**: The command verifies both indices, retrieves the corresponding startup and note, and removes the note from the startup's note list.
3. **Result Generation**: A confirmation message is returned, indicating successful deletion.

**Sequence Diagram for DeleteNote Command**:

<puml src="diagrams/DeleteNoteSequenceDiagram.puml" alt="DeleteNote" />

### Common Features Across Note Commands

- **Validation**: Each command ensures that the indices provided are within the valid range before proceeding with modifications. If not, an error message is shown.
- **Model Update**: All commands interact with the `Model` to either add, modify, or delete notes, ensuring that all changes are reflected.

These commands collectively provide robust functionality for managing detailed annotations on startups, facilitating better information management within CapitalConnect.

### Add and Edit Person Commands

### Add Person Command
The add person command allows users to add key employees' information into a startup.

The key employee information will be displayed in the people pane, next to the startup card view. Through this function, users can keep track of employees' name, email, and other related information.

A typical program flow is as follows:
1. User enters a command to add a key employee to the first startup, e.g. `add-p 1 pn/John pe/johndoe@example.com pd/founder`.
2. The input is passed to the `AddressbookParser` class which calls `AddPersonCommandParser`, and then the `AddPersonCommandParser` parses the keywords from the user's input.
3. The `AddPersonCommandParser` checks whether all required fields are entered and whether the index is valid. If all checks are passed, the program will move onto `AddPersonCommand`.
4. If the key employee does not exist in the startup, the startup's employee information will then be updated to include the new person.

The following sequence diagram illustrates the process of execution of an add person command.

<puml src="diagrams/AddPerson.puml" alt="AddPerson" />

### Edit Person Command
The edit person command allows users to edit key employees' information from a startup.

The key employee information will be displayed in the people pane, next to the startup card view. Through this function, users can keep track of employees' name, email, and other related information.

A typical program flow is as follows:
1. User enters a command to edit a key employee of the first startup, e.g. `edit-p 1 1 pn/John pe/johndoe@example.com pd/founder`.
2. The input is passed to the `AddressbookParser` class which calls `EditPersonCommandParser`, and then the `EditPersonCommandParser` parses the keywords from the user's input.
3. The `EditPersonCommandParser` checks whether all required fields are entered and whether the both indexes are valid. If all checks are passed, the program will move onto `EditPersonCommand`.
4. If the EditPersonCommand doesn't cause duplicate key employee to exist in the startup (a duplicate key employee is noted by having the same email as an existing key employee) the startup's employee information will then be updated.

The following activity diagram illustrates the process of execution of an edit person command.

<puml src="diagrams/EditPersonActivityDiagram.puml" alt="EditPerson" />


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope


**Target user profile**: A venture capital portfolio manager who

* has a need to keep a significant amount of startup investments
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Venture capital firms manage diverse portfolios of startup
investments across various industries. The app streamlines the management of startup
investments, enabling VC firms to easily add, categorize, and track a diverse
portfolio of investments in various industries and funding stages.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​           | I want to …​                                        | So that I can…​                                                                               |
|----------|-------------------|-----------------------------------------------------|-----------------------------------------------------------------------------------------------|
| `* * *`  | new user          | see usage instructions                              | refer to instructions when I forget how to use the App                                        |
| `* * *`  | user              | view the startup investments in my portfolio        | see the list of startup investments that I'm interested in                                    |
| `* * *`  | user              | add a new startup investment to my portfolio        | save the details of the new startup investment                                                |
| `* * *`  | user              | delete a startup investment to my portfolio         | remove the startup investment that I am no longer interested in                               |
| `* *`    | user              | find a startup investment by names                  | locate a startup investment by its name without having to go through the entire list          |
| `* *`    | user              | find a startup investment by industries             | locate a startup investment by its industry without having to go through the entire list      |
| `* *`    | user              | find a startup investment by funding stages         | locate a startup investment by its funding stage without having to go through the entire list |
| `* *`    | user              | edit a startup investment in my portfolio           | update a startup information in my portfolio                                                  |
| `* *`    | intermediate user | add a note to the startups I'm interested in        | know more about the startup investment when checking it through the app                       |
| `* *`    | intermediate user | edit a note of the startups I'm interested in       | update the startup investment in the app                                                      |
| `* *`    | intermediate user | delete a note of the startups I'm interested in     | get rid of redundant information                                                              |
| `* *`    | intermediate user | add key employee's information to startups          | know more about the startup through its people                                                |
| `* *`    | intermediate user | edit key employee's information from the startups   | update the startups' employees' information                                                   |
| `* *`    | intermediate user | delete key employee's information from the startups | remove outdated employee information from a startup                                           |


### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

#### Use case: UC1 - Add a startup investment

**MSS**

1.  User requests to add a new startup investment to their portfolio.
2.  CapitalConnect dashboard prompts the user to provide details including:
    * Startup name
    * Industry
    * Funding stage
    * Address
    * Contact information
    * Valuation of startup
3.  User provides the necessary details.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect adds the new startup's profile to the user's portfolio in the dashboard.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 4b. Startup name already exists in the portfolio.

    * 4b1. CapitalConnect notifies the user about the duplicate entry.

      Use case resumes at step 2.


#### Use case: UC2 - Search for startup investments by names

**MSS**

1.  User requests to search for startup investments by names.
2.  CapitalConnect dashboard prompts the user to input the names of the startup.
3.  User provides the names of the startup.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect searches for startup investments matching the specified names in the user's portfolio.
6.  CapitalConnect displays the startup investments matching the search criteria.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.


#### Use case: UC3 - Search for startup investments by industries

**MSS**

1.  User requests to search for startup investments by industries.
2.  CapitalConnect dashboard prompts the user to input the industries.
3.  User provides the industries.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect searches for startup investments matching the specified industries in the user's startup portfolio.
6.  CapitalConnect displays the startup investments matching the search criteria.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.


#### Use case: UC4 - Search for startup investments by funding stages

**MSS**

1.  User requests to search for startup investments by funding stages.
2.  CapitalConnect dashboard prompts the user to input the funding stage.
3.  User provides the funding stage.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect searches for startup investments matching the specified funding stages in the user's startup portfolio.
6.  CapitalConnect displays the startup investments matching the search criteria.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.


#### Use case: UC5 - Save the current state of CapitalConnect dashboard

**MSS**

1.  CapitalConnect automatically saves the state of the dashboard after every add and delete operation.

    Use case ends.

**Extensions**

* 1a. Issue with saving the dashboard state.

    * 1a1. CapitalConnect shows an error message indicating the issue with saving the dashboard state.

      Use case ends.

#### Use case: UC6 - Delete a startup investment from the portfolio

**MSS**

1.  User requests to delete a specific startup investment from their portfolio.
2.  CapitalConnect dashboard prompts the user to input the index of the startup investment to be deleted.
3.  User provides the index of the startup investment.
4.  CapitalConnect verifies the input for validity.
5.  CapitalConnect deletes the startup investment at the specified index from the user's portfolio.
6.  CapitalConnect displays a confirmation message indicating successful deletion of the startup investment.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 5a. Specified index is out of range or no startup investments at the specified index.

    * 5a1. CapitalConnect shows an error message indicating the issue.

      Use case ends.

#### Use case: UC7 - Edit a startup investment from the portfolio

**MSS**

1.  User requests to edit a specific startup investment from their portfolio.
2.  CapitalConnect dashboard prompts the user to input the index of the startup investment to be edited and updated details.
3.  User provides the index of the startup investment and the updated details, where updated details must be one or more from the following:
    * Startup name
    * Industry
    * Funding stage
    * Address
    * Contact information
    * Valuation of Startup
4. CapitalConnect verifies the updated details for validity.
5. CapitalConnect verifies the index for validity.
6. CapitalConnect edits the startup investment at the specified index from the user's portfolio.
7. CapitalConnect displays a confirmation message indicating successful edit of the startup investment.

    Use case ends.

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case ends.

* 5a. Specified index is out of range or no startup investments.

    * 5a1. CapitalConnect shows an error message indicating the issue.

      Use case ends.

#### Use case: UC8 - List all startups in CapitalConnect

**MSS**
1. User requests to list all startups in CapitalConnect.
2. CapitalConnect displays all startups.

**Use case ends.**

#### Use case: UC9 - Clear all startups in CapitalConnect

**MSS**
1. User requests to clear all startups in CapitalConnect.
2. CapitalConnect resets all current data and clears all the startups.
3. CapitalConnect displays a empty list of startups.

**Use case ends.**

#### Use Case: UC10 - Add a Note to a Startup

**MSS**

1. User requests to add a note to a specific startup in their portfolio.
2. CapitalConnect dashboard prompts the user to input the index of the startup and the note content.
3. User provides the index of the startup and the note content.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect adds the note to the startup at the specified index in the user's portfolio.
6. CapitalConnect displays a confirmation message indicating successful addition of the note.

**Use case ends.**

**Extensions**

4a. Invalid input or missing parameters.
- 4a1. CapitalConnect shows an error message.
- **Use case resumes at step 2.**

5a. Specified index is out of range or no startup at the specified index.
- 5a1. CapitalConnect shows an error message indicating the issue.
- **Use case ends.**

#### Use Case: UC11 - Edit an Existing Note of a Startup

**MSS**

1. User requests to edit an existing note of a specific startup in their portfolio.
2. CapitalConnect dashboard prompts the user to input the index of the startup and the index of the note, along with the new content for the note.
3. User provides the necessary indices and the new note content.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect updates the note at the specified index with the new content in the startup's notes list.
6. CapitalConnect displays a confirmation message indicating successful modification of the note.

**Use case ends.**

**Extensions**

4a. Invalid input or missing parameters.
- 4a1. CapitalConnect shows an error message.
- **Use case resumes at step 2.**

5a. Specified startup index or note index is out of range or no such note exists.
- 5a1. CapitalConnect shows an error message indicating the issue.
- **Use case resumes at step 2.**

#### Use Case: UC12 - Delete a Note from a Startup

**MSS**

1. User requests to delete a specific note from a startup in their portfolio.
2. CapitalConnect dashboard prompts the user to input the index of the startup and the index of the note to be deleted.
3. User provides both indices.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect deletes the note at the specified index from the startup's notes list.
6. CapitalConnect displays a confirmation message indicating successful deletion of the note.

**Use case ends.**

**Extensions**

4a. Invalid input or missing parameters.
- 4a1. CapitalConnect shows an error message.
- **Use case resumes at step 2.**

5a. Specified startup index or note index is out of range or no such note exists.
- 5a1. CapitalConnect shows an error message indicating the issue.
- **Use case resumes at step 2.**

#### Use Case: UC13 - Add a Person to a Startup

**MSS**

1. User requests to add a person to a specific startup in their portfolio.
2. CapitalConnect dashboard prompts the user to provide the index of the startup and the person details including:
    * Person name
    * Person email
    * Description (optional)
3. User provides the index of the startup and the necessary details.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect adds the person to the startup at the specified index in the user's portfolio.
6. CapitalConnect displays a confirmation message indicating successful addition of the note.

**Use case ends.**

**Extensions**

* 4a. Invalid input or missing parameters.

    * 4a1. CapitalConnect shows an error message.

      Use case resumes at step 2.

* 4b. Person email already exists in the startup's person list.

    * 4b1. CapitalConnect notifies the user about the duplicate entry.

      Use case resumes at step 2.


#### Use Case: UC14 - Edit an Existing Person in a Startup

**MSS**

1. User requests to edit an existing person in a specific startup in their portfolio.
2. CapitalConnect dashboard prompts the user to input the index of the startup and the index of the person, along with the details for the person, where updated details must be one or more from the following:
    * Person name.
    * Person email.
    * Description.
3. User provides the indices of the startup investment and the person, along with the updated person details.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect updates the person at the specified index with the new content in the startup's person list.
6. CapitalConnect displays a confirmation message indicating successful modification of the person.

**Use case ends.**

**Extensions**

* 4a. Invalid input or missing parameters.
    * 4a1. CapitalConnect shows an error message.
      Use case resumes at step 2.

* 4b. Edited person email already exists in the startup's person list.

    * 4b1. CapitalConnect notifies the user about the duplicate entry.

      Use case resumes at step 2.

#### Use Case: UC15 - Delete a Person from a Startup

**MSS**

1. User requests to delete a specific person from a startup in their portfolio.
2. CapitalConnect dashboard prompts the user to input the index of the startup and the index of the person to be deleted.
3. User provides both indices.
4. CapitalConnect verifies the input for validity.
5. CapitalConnect deletes the person at the specified index from the startup's person list.
6. CapitalConnect displays a confirmation message indicating successful deletion of the person.

**Use case ends.**

**Extensions**

* 4a. Invalid input or missing parameters.
    * 4a1. CapitalConnect shows an error message.
      Use case resumes at step 2.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 startups without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The system should have a response time of less than 1 second for common operations, such as adding or deleting a startup investment, to provide a smooth user experience.
5.  The application should provide clear and user-friendly error messages in case of invalid input or system errors to assist users in troubleshooting issues efficiently.
6.  The dashboard interface should be responsive and adapt to different screen sizes, ensuring a seamless user experience across devices such as desktops, laptops, tablets, and smartphones.


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **CapitalConnect dashboard**: The user interface of the CapitalConnect application where users can manage their startup investments, perform searches, and view their portfolio.
* **Startup investment**: An investment made by a user in a startup company, typically including details such as the startup name, industry, funding stage, address, and contact information.
* **Industry**: The sector or field in which a startup operates, such as Tech, Biotech, or Finance.
* **Funding stage**: The development stage of a startup at which it has received a certain level of investment, such as Pre-Seed, Seed, Series A, Series B or Series C.
* **Valuation**: The valuation of the company.
* **Dashboard state**: The current configuration and data displayed in the CapitalConnect dashboard, including startup investments and any applied filters or search results.
* **Index**: A numeric value representing the position of an item within a list, used in commands to reference specific startup investments in the portfolio.
* **Confirmation message**: A notification displayed to the user indicating the successful completion of an action, such as adding or deleting a startup investment.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   2. Double-click the jar file <br>
      Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

2. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Getting help

1. Get help message on the usage of CapitalConnect

   1. Test case: `help` <br>
       Expected: A help box will pop up showing the link to the CapitalConnect user guide.

   2. Test case: `help 1234`<br>
       Expected: A help box will pop up showing the link to the CapitalConnect user guide.

### Adding a startup

1. Adding a startup with valid inputs.

   1. Test case: `add n/Google p/999 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: The startup with the respective details have been successfully added to CapitalConnect. Details of the added startup shown.

2. Adding a startup with missing / invalid inputs.

    1. Test case: `add n/ p/98765432 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: No startup is added, message sent to user that name should not be blank.

    2. Test case: `add n/Google p/1 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/A i/tech`<br>
        Expected: No startup is added, message sent to user that phone number should be at least 3 digits long.

    3. Test case: `add n/Google p/98765432 e/sundarpichal@example.com v/100000 a/Menlo Park, block 123, #01-01 f/J i/tech`<br>
        Expected: No startup is added, message sent to user on appropriate inputs for funding stages.

    4. Test case: `add n/Google p/98765432 e/sundarpichal@example.com v/-1 a/Menlo Park, block 123, #01-01 f/A i/tech` <br>
        Expected: No startup is added, message sent to user on the valid inputs for company valuation.

### Deleting a startup

1. Deleting a startup while all startups are being shown

    1. Prerequisites: List all startups using the `list` command. Multiple startups in the list.

    2. Test case: `delete 1`<br>
       Expected: First startup is deleted from the list. Details of the deleted startup shown.

    3. Test case: `delete 0`<br>
       Expected: No startup is deleted. Error details shown in the status message. Status bar remains the same.

    4. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.
   
2. Deleting a startup after `find` command is performed

    1. Prerequisites: A startup with the name `Apple` exists. Find the startup using the `find n/apple` command.
       Startups in the list are shown.

    2. Test case: `delete 1`<br>
       Expected: First startup displayed on the list is deleted. Details of the deleted startup shown.

    3. Test case: `delete 0`<br>
       Expected: No startup is deleted. Error details shown in the status message. Status bar remains the same.

    4. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

### Editing a startup

1. Prerequisites: One startup in CapitalConnect at the first position.

2. Editing startup with valid inputs

    1. Test case: `edit 1 n/test` <br>
        Expected: The startup at position 1 has its name changed. Details of the edited startup is shown.

    2. Test case: `edit 1 v/9999` <br>
        Expected: The startup at position 1 has its valuation changed, details of the edited startup is shown. The new valuation is displayed as `9.9k`.

3. Editing a startup with invalid inputs

    1. Test case: `edit 1 f/H` <br>
        Expected: No edits made to any startups, users are informed on valid input for funding stages.

    2. Test case: `edit 1 i/`
        Expected: No edits made to any startups, users are informed on valid industry inputs.

### Finding a startup

1. Prerequisites: One startup named `Apple` in `tech` industry and with a funding stage `A` is presented in CapitalConnect.

2. Finding a startup with valid inputs.

    1. Test case: `find n/Apple`<br>
       Expected: The startup with the respective name has been successfully displayed in CapitalConnect.

    2. Test case: `find i/tech`<br>
       Expected: The startup with the respective industry has been successfully displayed in CapitalConnect.

    3. Test case: `find f/A`<br>
       Expected: The startup with the respective funding stage has been successfully displayed in CapitalConnect.

3. Finding a startup with missing / invalid inputs.

    1. Test case: `find n/`<br>
       Expected: No startup is displayed, message of invalid command format is sent to user as names should not be blank.

    2. Test case: `find i/`<br>
       Expected: No startup is displayed, message of invalid command format is sent to user as industries should not be blank.

    3. Test case: `find f/`<br>
       Expected: No startup is displayed, message of invalid command format is sent to user as funding stages should not be blank.

    4. Test case: `find` <br>
       Expected: No startup is displayed, message of invalid command format is sent to user as given keywords should not be blank.

### Listing Startups

1. Viewing all startups in CapitalConnect

    1. Prerequisites: There are startups stored in CapitalConnect.

    2. Test case: `list` <br>
       Expected: all previously saved startups will be shown only in the startup list. Success message is displayed.

    3. Test case: `list 1234`<br>
       Expected: all previously saved contacts will be shown only in the startup list. Success message is displayed.

### Adding a Note to a Startup

1. Prerequisites: One startup in CapitalConnect at the first position with 0 or more notes

2. Adding a note with valid inputs

    1. Test case: `addnote 1 Beautiful and Handsome` <br>
       Expected: The startup at position 1 has a note added to it.
       Details of the Note is shown in the Note Box when the startup card with index 1 is selected.

3. Adding a note with invalid inputs

    1. Test case: `addnote 1` <br>
       Expected: No note is added to the Startup and Note Box, users are informed on valid input for the addnote command.

    2. Test case: `addnote No index given` <br>
       Expected: No note is added to the Startup and Note Box, users are informed on valid input for the addnote command.


### Editing a Note in a Startup

1. Prerequisites: A startup in CapitalConnect in position 1 with at least one note.

2. Editing a note with valid inputs

    1. Test case: `editnote 1 1 New content for the note`
       Expected: The first note of the startup at index 1 is edited to "New content for the note".
       Details of the Note is shown in the Note Display Box when the startup card with index 1 is selected.
   
3. Editing a note with invalid inputs

    1. Test case: `editnote` <br>
       Expected: No note is edited. Error message about invalid command format is shown.

    2. Test case: `editnote 1` <br>
       Expected: No note is edited. Error message about invalid command format is shown.

    3. Test case: `editnote 1 2` <br>
       Expected: No note is edited. Error message about invalid command format is shown.

    4. Test case: `editnote 1 99 New content but invalid note index` <br>
       Expected: No note is edited as the note index is out of range. Error message about invalid note index is shown.

    5. Test case: `editnote 99 1 New content but invalid startup index` <br>
       Expected: No note is edited as the startup index is out of range. Error message about invalid startup index is shown.

### Deleting a Note from a Startup

1. Prerequisites: A startup in CapitalConnect in position 1 with at least one note.

2. Deleting a note with valid inputs

    1. Test case: `deletenote 1 1` <br>
       Expected: The first note of the startup at index 1 is deleted.
       Details of the updated notes list are shown in the Note Box when the startup card with index 1 is selected.

3. Deleting a note with invalid inputs

    1. Test case: `deletenote` <br>
       Expected: No note is deleted. Error message about invalid command format is shown.

    2. Test case: `deletenote 1` <br>
       Expected: No note is deleted. Error message about invalid command format is shown.

    3. Test case: `deletenote 1 99` <br>
       Expected: No note is deleted as the note index is out of range. Error message about invalid note index is shown.

    4. Test case: `deletenote 99 1` <br>
       Expected: No note is deleted as the startup index is out of range. Error message about invalid startup index is shown.

### Adding a Person to a Startup

1. Adding a person to a startup with valid input

   1. Prerequisites: There must be at least one startup stored in CapitalConnect.

   2. Test case (compulsory fields only): `add-p 1 pn/John pe/johndoe@gmail.com` <br>
      Expected: The startup at position 1 has a person added to it. Details of the `Person` is shown in the key employee
      box if the startup card with index 1 is selected

   3. Test case (all fields specified): `add-p 1 pn/Joe pe/joe@gmail.com pd/founder` <br>
      Expected: The `Startup` at position 1 has a `Person` added to it. Details of the `Person` is shown in the key employee
      box if the startup card with index 1 is selected.
   
   4. Test case (multiple descriptions specified): `add-p 1 pn/James pe/james@gmail.com pd/Founder pd/Harvard graduate` <br>
      Expected: The `Startup` at position 1 has a `Person` added to it. Details of the `Person` is shown in the key employee
      box if the startup card with index 1 is selected.
   
   5. Test case (duplicate descriptions specified): `add-p 1 pn/Jane pe/jane@gmail.com pd/Founder pd/Founder` <br>
      Expected: The `Startup` at position 1 has a `Person` added to it. The added `Person` should contain only one description `Founder`, since the two inputted description are duplicate.
      Details of the `Person` is shown in the key employee box if the startup card with index 1 is selected.

2. Adding a person to a startup with invalid input

   1. Prerequisites: There must be at least one startup stored in CapitalConnect. For our example we assume there are
      less than 50 startups stored in CapitalConnect and a person with the email `johndoe@gmail.com` is already stored inside
      the startup with index 1.

   2. Test case (missing compulsory field): `add-p 1 pe/jane@gmail.com pd/founder` <br>
      Expected: No `Person` added to the `Startup`. Error details shown in the status message. The key employee box remains unchanged.
   
   3. Test case (invalid index): `add-p 99 pn/Amy pe/amy@gmail.com pd/founder` <br>
      Expected: No `Person` added to the `Startup`. Error details shown in the status message. The key employee box remains unchanged.
   
   4. Test case (duplicate email): `add-p 1 pn/Jess pe/johndoe@gmail.com pd/founder` <br>
      Expected: No `Person` added to the `Startup`. Error details shown in the status message. The key employee box remains unchanged.
   
   5. Test case (repeated single-valued fields): `add-p 1 pn/Jess pn/James pe/johndoe@gmail.com pd/founder` <br>
      Expected: No `Person` added to the `Startup`. Error details shown in the status message. The key employee box remains unchanged.
   
   6. Test case (invalid format for one field): `add-p 1 pn/Amy* pe/johndoe@gmail.com pd/founder` <br>
      Expected: No `Person` added to the `Startup`. Error details shown in the status message. The key employee box remains unchanged.
   
   7. Other incorrect `add-p` commands to try: `add-p 1 pn/`, `add-p`
      Expected: similar to previous


### Editing a Person in a Startup

1. Editing a person in a startup with valid input

   1. Prerequisites: The startup at index 1 contains at least 1 person.
      No person inside the startup at index 1 has the email `josh@gmail.com` and `jay@gmail.com`
   
   2. Test case (name specified): `edit-p 1 1 pn/John` <br>
      Expected: The name of the first person inside the startup at index 1 gets edited to `John`. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected
   
   3. Test case (email specified): `edit-p 1 1 pe/josh@gmail.com` <br>
      Expected: The email of the first person inside the startup at index 1 gets edited to `josh@gmail.com`. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected
   
   4. Test case (description specified): `edit-p 1 1 pd/ceo` <br>
      Expected: The description of the first `Person` inside the `Startup` at index 1 gets edited to `ceo`. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected.
   
   5. Test case (empty description specified): `edit-p 1 1 pd/` <br>
      Expected: The description of the first `Person` inside the `Startup` at index 1 is removed. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected.
   
   6. Test case (multiple description specified): `edit-p 1 1 pd/founder pd/new` <br>
      Expected: The first `Person` inside the `Startup` at index 1 has two descriptions, which are `founder` and `new`. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected.
   
   7. Test case (duplicate description specified): `edit-p 1 1 pd/founder pd/founder` <br>
       Expected: The first `Person` inside the `Startup` at index 1 only has one description `founder`, since the specified tags are duplicate. Details of the updated `Person` is shown in the key employee
       box if the startup card with index 1 is selected.
   
   8. Test case (all fields specified): `edit-p 1 1 pn/Jay pe/jay@gmail.com pd/founder` <br>
      Expected: The details of the first person inside the startup at index 1 gets edited. Details of the updated `Person` is shown in the key employee
      box if the startup card with index 1 is selected.

2. Editing a person in a startup with invalid input

    1. Prerequisites: For our example, we assume there are less than 10 startups stored in CapitalConnect and less
       than 10 person stored in the startup at index 1. We also assume a person with the email `johndoe@gmail.com` is
       already stored inside the startup with index 1.
   
    2. Test case (missing person index): `edit-p 1 pn/name pd/founder` <br>
       Expected: No `Person` gets edited. Error details shown in the status message.
   
    3. Test case (invalid startup index): `edit-p 99 1 pn/Amy pe/amy@gmail.com pd/founder` <br>
       Expected: No `Person` gets edited. Error details shown in the status message.
   
    4. Test case (invalid person index): `edit-p 1 50 pn/Amy pe/amy@gmail.com pd/founder` <br>
       Expected: No `Person` gets edited. Error details shown in the status message.
   
    5. Test case (duplicate email): `edit-p 1 1 pn/Jess pe/johndoe@gmail.com pd/founder` <br>
       Expected: No `Person` gets edited. Error details shown in the status message.
   
    6. Test case (invalid format for one field): `edit-p 1 1 pn/Amy* pe/johndoe@gmail.com pd/founder` <br>
       Expected: No `Person` gets edited. Error details shown in the status message.
   
    7. Other incorrect `edit-p` commands to try: `edit-p 1 1 pn/`, `edit-p`, `edit-p 1 1 pe/email` <br>
       Expected: similar to previous

### Deleting a Person from a Startup

1. Deleting a person from a startup with valid input

    1. Prerequisites: The startup at index 1 contains at least 1 person.
   
    2. Test case : `delete-p 1 1` <br>
       Expected: The first person inside the startup at index 1 gets deleted.
       Details of the `Person` is removed from the key employee box if the startup card with index 1 is selected

2. Deleting a person from a startup with invalid input

   1. Prerequisites: For our example, we assume there are 10 startups stored in CapitalConnect and there are 10 person stored in the startup at index 1. 
   
   2. Test case (invalid startup index): `delete-p 99 1` <br>
     Expected: No `Person` gets deleted. Error details shown in the status message.
   
   3. Test case (invalid person index): `delete-p 1 50` <br>
     Expected: No `Person` gets deleted. Error details shown in the status message.

   4. Test case (missing person index): `edit-p 1 pn/name pd/founder` <br>
      Expected: No `Person` gets deleted. Error details shown in the status message.

   5. Test case (no index specified): `delete-p` <br>
      Expected: No `Person` gets deleted. Error details shown in the status message.

### Clearing All Startups Data

1. Clearing all startups data in CapitalConnect

    1. Prerequisites: There are startups stored in CapitalConnect.

    2. Test case: `clear` <br>
       Expected: All previously saved startups will be removed from the startup list. Success message is displayed.

    3. Test case: `clear 1234`<br>
       Expected: All previously saved startups will be removed from the startup list. Success message is displayed.


### Saving Startups Data

1. Saving startups data stored in CapitalConnect

    1. Prerequisites: There are startups stored in CapitalConnect.

    2. Close the program using the `exit` command or by double-clicking the close button.

    3. Reopen the app
       Expected: The app should display all previously stored startup data.

## **Appendix: Planned Enhancement**

Team size: 4

1. Limit length of startup name: Currently we do not limit length of startup name user can input. This
results in overflow. We plan to address this in the future by limiting length of startup names to 32 letters long.

2. Supporting country codes: Currently we do not allow users to specify country codes using "-", this makes it hard to track
international numbers. We plan to allow for the user to add an optional country code field alongside the phone number in further iterations.

3. Limit phone numbers: Currently we do not limit the length of phone numbers users can input. This may result in a UI overflow.
We plan to address this in the future by limiting the length of the phone number to 8 digits for the number and 3 digits for the country code after planned enhancement 2 has been completed.

4. Supporting non-alphanumerical characters in startup names: Some company names may contain non-alphanumerical characters. We plan to address this in the future by allowing for such characters to be in the name,
but also changing the current regular expression rules to ensure that the input remains valid.

5. Supporting non-alphanumerical characters in tags: Currently tags must be in alphanumerical characters without spacings. This forces users to find alternative means to add tags, such as using camel case within their tags instead. For example,
`US based` would not be an allowed tag, resorting in users tagging the startup as `USBased` instead. We plan to address this in the future by allowing for such characters to be in the tag,
but also changing the current regular expression rules to ensure that the input remains valid.

6. Supporting Indexes for notes: Currently, notes displayed do not contain information about their indexes, making editing and deleting them not as straightforward and requires remembering their index or counting from the top. We plan to address this in a future iteration.

7. Supporting partial matching in find commands: We understand that you might want to use partial matching to find matching startups, but this feature is currently under development. This feature will be dropped soon!

8. Potential limitation in detecting duplicated persons: Note that duplicated persons in one startup are detected by `pe/EMAIL`. We assume that email is unique for every person. In other words, we assume that it is possible to have 3 Johns in one company, and they all have different emails. Before adding a new person to the startup, always double-check their email to make sure that the person is not added already. Also take note that we allow one person to work in multiple startups.
