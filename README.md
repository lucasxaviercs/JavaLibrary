# 📚 SCC0504 JavaLibrary Project
A simple Library Management System designed to demonstrate CRUD operations, core Object-Oriented Programming and GUI (Graphical User Interface)
- Technologies used: Java + Swing
  
Alunos
- Leonardo Brito da Silva
- Lucas Xavier Carvalho Santos
- Yan Barbosa Servilha
---
## Project Description
JavaLibrary is a desktop application that simulates the core operations of a library management system. It allows librarians to manage books, register patrons, and handle loan transactions using CSV file persistence so data survives between sessions.

### Features
**Book Management**
- Add new books with title, author, ISBN, and number of copies
- Edit existing book information
- Delete books from the catalog
- Real-time search by title, author, or ISBN
- Automatic tracking of total vs. available copies

**Patron Management**
- Register new library members with name and contact info
- Edit and delete patron records
- Search patrons by name or ID
- View a patron's full loan history
- Deletion is blocked if the patron has active loans

**Loan Management**
- Check out a book to a patron (auto-generates loan ID, sets a 14-day due date)
- Return a book by selecting its loan record
- Search loans by book title or patron name
- Automatic fine calculation ($1.50/day) for overdue loans
- Clear status display: Active or Returned

**Data Persistence**
- All records are automatically saved to and loaded from CSV files (`data/books.csv`, `data/patrons.csv`, `data/loans.csv`)

---
## How to Run the Program
### Prerequisites
- **Java JDK 11 or higher** installed on your machine
- A terminal / command prompt

You can check your Java version with:
```bash
java -version
```

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/your-username/JavaLibrary.git
cd JavaLibrary
```

**2. Compile all source files**

From the project root (the folder containing `Main.java` and the `data/` directory), run:
```bash
javac -d . controller/*.java exception/*.java model/*.java persistence/*.java view/*.java Main.java
```
This will compile all `.java` files and place the `.class` files in their corresponding package folders.

**3. Run the application**
```bash
java Main
```
The JavaLibrary window will open automatically.

> **Note:** The `data/` folder must exist in the same directory you run the command from. It contains the CSV files used for persistence. If any CSV file is missing, the app will start with an empty list for that entity and create the file on the first save.

### Project Structure
```
JavaLibrary/
├── Main.java
├── controller/
│   ├── BookController.java
│   ├── LoanController.java
│   └── PatronController.java
├── exception/
│   ├── BookAlreadyOnLoanException.java
│   ├── PatronHasActiveLoansException.java
│   └── PersistenceException.java
├── model/
│   ├── Book.java
│   ├── Loan.java
│   └── Patron.java
├── persistence/
│   └── FileManager.java
├── view/
│   ├── BooksPanel.java
│   ├── LoanPanel.java
│   ├── MainWindow.java
│   └── PatronsPanel.java
└── data/
    ├── books.csv
    ├── loans.csv
    └── patrons.csv
```

---

## Code Overview

The project is divided into packages, each with a clear and specific responsibility

### `model/`

They are classes that represent a book, a patron, and a loan transaction. Each one stores information and provides getters and setters to read or modify it.

- **`Book`** : holds a book's title, author, ISBN, total copies, and available copies.
- **`Patron`** — holds a member's ID, name, and contact information.
- **`Loan`** — holds a reference to the `Book` and `Patron` involved, the loan date, the due date, and whether the book has been returned. It also calculates any overdue fine automatically based on today's date.

---

### `persistence/`

**`FileManager`** is the only class responsible for reading and writing files. It handles three CSV files: one for books, one for patrons, and one for loans.

When the app starts, `FileManager` reads each CSV line by line and converts each row into a `Book`, `Patron`, or `Loan` object. When data changes, the controller calls `FileManager` again to overwrite the file with the updated list.

This means no other part of the application touches files directly. If something goes wrong during a read or write, `FileManager` throws a `PersistenceException` that the GUI catches and shows as an error message.

---

### `exception/`

Instead of using generic error messages, the project defines three specific exception classes:

- **`BookAlreadyOnLoanException`**: thrown when someone tries to check out a book with no available copies.
- **`PatronHasActiveLoansException`**: thrown when someone tries to delete a patron who still has unreturned books.
- **`PersistenceException`**: thrown when a file read or write fails.

---

### `controller/`

Controllers sit between the GUI and the data. They receive requests from the view, validate the input, update the model objects, and tell `FileManager` to save the changes. There is one controller per entity:

- **`BookController`**: manages the list of books. Validates that a new book has a title, author, ISBN, and at least one copy before adding it. Also prevents duplicate ISBNs and blocks updates that would set total copies below the number currently on loan.
- **`PatronController`**: manages the list of patrons. Checks for empty names on add/edit, and checks for active loans before allowing deletion.
- **`LoanController`**: manages checkout and return operations. It depends on both `BookController` (to check availability and update copy counts) and `PatronController` (to verify the patron exists). It auto-generates sequential loan IDs and sets a 14-day due date on every checkout.

---

### `view/`

The view package contains all the Swing code that the user actually sees. It is split into four classes:

- **`MainWindow`**:the main application window. It creates one instance of each controller and one instance of each panel, then organizes the panels into tabs
- **`BooksPanel`**: contains the search bar, a table listing all books, and the Add / Edit / Delete buttons.
- **`PatronsPanel`**: contains the search bar, a table listing all patrons, and the Add / Edit / Delete buttons, plus a "View History" button that shows a patron's past loans.
- **`LoanPanel`**: contains the loan table and the Check Out / Return buttons.

Each panel holds a reference to its matching controller. When the user clicks a button, the panel collects the input (from a dialog or the selected table row), calls the controller, and refreshes the table. If the controller throws an exception, the panel catches it and shows a `JOptionPane` error dialog
