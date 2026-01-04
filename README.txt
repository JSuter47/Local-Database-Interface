1. Project Description

This project is a command-line database management system (DBMS) for a drone-based delivery and equipment rental service.
It provides functionality to:

	Add new entities (drones, equipment, members, rental transactions, etc.)

	Update or delete existing records

	Schedule deliveries and pickups using drones

	Record equipment returns

	Generate reports using SQL queries

	Search and list database contents

All menus are text-driven and accessed through a single main interface.
The database engine used is SQLite, and the system connects to a local database file automatically when run.

2. Requirements

	VS Code with the Java Extension Pack installed

	Java 17

	No external libraries required; SQLite JDBC driver is included with Java’s standard driver manager behavior for SQLite files.

3. Importing the Project Into VS Code

Follow these steps exactly to load and run the project:

Step 1 — Unzip the project folder

	Unzip the entire project archive so that the source files and directory structure are preserved.
	Make sure you know where the unzipped folder is located.

Step 2 — Open the project folder in VS Code

	Launch VS Code.

	Go to the File menu.

	Click Open Folder…

	Select the unzippped project folder (the folder that contains /src, /options, /utilities, etc.).

	Click Open.

	VS Code will load the folder as a Java project and automatically configure the workspace.

Step 3 — Allow VS Code to build the project

	You may see prompts such as “Import Java project?” or “Build workspace?”.
	Choose Yes for all prompts.

4. Running the Program

Step 1 — Locate the main file

	In the Explorer sidebar, navigate to:

	src
	> DBMSPrimaryInterface.java

Step 2 — Run the program

	Open DBMSPrimaryInterface.java.
	At the top right of the editor window, click Run (green play button).
	Or right-click inside the file and choose Run Java.

This will:

	Initialize the SQLite database file (DroneDeliveryDBS.db)
	Launch the main menu in the terminal panel

5. Navigating the Program

After running the main interface, you will see a numbered menu.
Use the number keys to choose options such as:

	(1) Manage Rentals
	(2) Create New Entity
	(3) Edit or Delete Items
	(4) Search
	(5) List all entities
	(6) Reports
	(0) Exit

Most menus follow the same pattern: enter the corresponding number and follow the prompts.
Data entry uses simple text input and validation. Database changes occur immediately through SQLite using prepared statements.

6. Notes

The database file (DroneDeliveryDBS.db) is created automatically on first run.
If you delete the file, the system will recreate an empty database.
Reports use SQL queries and output directly to the terminal.
The project structure was designed for clarity and modularity; each menu subsystem is located in the options package.