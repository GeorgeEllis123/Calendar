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

This program does not support: 
* Multiple calendars
* Deleting events 
* Edit when the days the repeating events will repeat

#### Assumptions: 
* The user will only use EST

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


Yazmin Alvarado: 
* Implemented the controller
* Implemented the command design pattern 
* Implemented the edit command 

