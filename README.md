## Calendar Program:

#### INSTRUCTIONS:
Select the way you would like to run this program. This program is compatible with both 
headless and interactive usage.
   1) Headless: 
      *  Create a res file and place a txt file inside with valid commands and end the file with an
   exit command.  
      * Ensure that the run configuration specifies the mode and file being used. 
   i.e. "--headless res/validCommands.txt "
      * The command line should show whether the commands worked.  If a command was invalid there 
   will be an accompanying message explaining what was wrong with the command. 
   2) Interactive: 
      * Create a run configuration that specifies that the mode is interactive. 
      i.e. "--interactive"
      * When the main is run, the user will be prompted to input a command.  
      * If the command is valid the user will see an appropriate message, otherwise the user will
   see an error that explains what the issue with the command was.  
      * Once the user is done inputting commands, type "exit" to end the program.
   3) JAR:
      * In your command line open to the directory your jar file is located.
      * Run the file using either: "java -jar Calendar3.jar --interactive" (for interactive)
      * or using "java -jar Calendar.jar --headless <filename>" (for headless). Make sure to
      * replace <filename> with an actual file name.

#### FEATURES: 
The user can:
* Create a single event with a unique subject and start time.  Single Events can be from a 
specified chunk of time or an all day event if the end date time is not specified (an all day event 
is 8am to 5pm).
* Create a repeating event with a unique subject and start time, that repeats on either the 
specified days or a certain amount of times. 
* Edit the subject, start date time, end date time, description, location, or status of an existing
event.
* Query the events so that the user can see existing events.
* Create a calendar where events will be stored.  Events cannot be made unless the user has created 
and is using a calendar. 
* Use a calendar.  Once a calendar is created a user can select which of the calendars they would 
like to use.
* Edit a calendar.  A user may edit the name or timezone of a calendar unless there is an existing 
calendar with that same name.
* Copy events.  A user may copy either one or a range of events and move them into another calendar,
adhering to that calendars timezone and the times that the user input. 

This program does not support:
* Deleting events 
* Edit when the days the repeating events will repeat

#### Distribution: 

Collaborative: 
* Implemented the model
* Implemented the classes and interfaces 
* Tested the IEvents
* Tested the CalendarModel
* Implemented the create command

George Ellis:
* Implemented the View
* Implemented the Main
* Implemented the query command
* Implemented the Model that can handle multiple calendars
* Tested the methods in the Model
* Implemented and tested, the new MultipleCalendarModel and ModifiableCalendar
* Updated old Model and IEvents as well as respective tests


Yazmin Alvarado: 
* Implemented the controller
* Implemented the command design pattern 
* Implemented the edit command 
* Implemented the controller that can handle multiple calendars
* Tested the commands in the Controller
* Implemented and tested, the new Commands, Controller, and Main

#### Changes To Old Design: 

As in lecture it was discussed we did not have to exactly follow open for extension/closed for 
modification, we made some slight changes to our old implementation for the IEvents. These changes
included: 

- A new getExactEvent method that only takes in a subject and start as our old 
method required an end date as well, and this was a problem for the copy events method as it
did not take an end date. 
- A new getStart method so that we could handle date offsets when
changing timezones. We chose this approach so our model could be delegated to handling basically
all the timezone work and it would just edit the start and endDate respectively. 
- Some new cases in the applyEditToBuilder method to help handle these required edits for changing 
timezones. 
- Previously the getEdittedEvent and editEvent method in the SeriesEvent were not implemented as 
they were not needed for our old design, but since we wanted to keep the metadata of SeriesEvents
when copying them over we decided to implement those previously missing methods. 
- We also changed the return type of the query-related methods in the IEvents as they did not store 
the metadata for SeriesEvents. This worked perfectly fine for our old implementation but now that we 
needed to keep metadata of copied events (assuming we wanted to reuse our old query commands to do 
so), this had to be changed. 
- This changed meant we had to make very slight modifications to our checkDuplicate methods as 
before it was guaranteed for events to be compared against SingleEvents, now it accounts for more 
generic cases. 
- We also had to update some of our old tests to fit this new return type, as well as make very 
minor modifications to the CalendarModel where it used those commands (they functionally worked the 
same just the return type was slightly different).

That leads us to the other changes we made where we followed a more open for extension/closed for
modification approach. All other higher level classes were made from extending old classes or using
the decorator design pattern. Because of this approach we had to make several helper methods and 
fields protected. All model updates where implemented using extension, while the new create
and edit commands that also could create and edit calendars used the decorator design pattern to
add additional functionality to the old already functioning versions. One other change we made
was moving some of the logic out of ACommand into a new abstract class called CommandParsing,
this was because we wanted our new classes to be able to use a different model but still have those
parsing helpers, but ACommand class requires you to use our old CalendarModel. So as bullet points:

- Made necessary helper methods and fields in CalendarModelImpl and CalendarControllerImpl 
protected (that were previously private) so they could be properly extended
- Moved parsing logic out of ACommand because it tied the user to a specific model interface 
(additional functionality couldn't be added)

Our approach to changing our previous code allows us to have 2 incremental functioning version of 
our highest-level Model, View, and Controller. As we did not touch any of the higher level commands
of the old Model, View, and Controller. The only thing that really changed in the old version was
our low level IEvent which for the most part was just changed by adding additional functionality
meaning it functions for both designs.