package pl.javastart.library.app;

import pl.javastart.library.app.exception.*;
import pl.javastart.library.io.ConsolePrinter;
import pl.javastart.library.io.DataReader;
import pl.javastart.library.io.file.FileManager;
import pl.javastart.library.io.file.FileManagerBuilder;
import pl.javastart.library.model.*;

import java.util.Comparator;
import java.util.InputMismatchException;

public class LibraryControl {
    private ConsolePrinter printer = new ConsolePrinter();
    private DataReader dataReader = new DataReader(printer);
    private FileManager fileManager;
    private Library library;

    LibraryControl() {
        fileManager = new FileManagerBuilder(printer, dataReader).built();
        try {
            library = fileManager.importData();
            printer.printLine("Zaimportowano dane z pliku");
        } catch (DataImportException | InvalidDataException e) {
            printer.printLine(e.getMessage());
            printer.printLine("Zainicjowano nową bazę.");
            library = new Library();
        }
    }

    public void controlLoop() {
        Option option;

        do {
            printOptions();
            option = getOption();
            switch (option) {
                case ADD_BOOK -> addBook();
                case ADD_MAGAZINE -> addMagazine();
                case PRINT_BOOKS -> printBooks();
                case PRINT_MAGAZINES -> printMagazines();
                case DELETE_BOOK -> deleteBook();
                case DELETE_MAGAZINE -> deleteMagazine();
                case ADD_USER -> addUser();
                case PRINT_USERS -> printUsers();
                case FIND_PUBLICATION -> findPublication();
                case EXIT -> exit();
           
            }
        } while (option != Option.EXIT);
    }

    private void findPublication() {
        printer.printLine("Podaj tytuł: ");
        String titleToFind = dataReader.getString();
        String messageIfNotFound = "Nie znaleziono publikacji o tytule " + titleToFind;
        library.getPublicationByTitle(titleToFind)
                .map(Publication::toString)
                .ifPresentOrElse((p)->printer.printLine(p), () -> printer.printLine(messageIfNotFound));

    }


    private Option getOption() {
        boolean optionOk = false;
        Option option = null;
        while (!optionOk) {
            try {
                option = Option.createFromInt(dataReader.getInt());
                optionOk = true;
            } catch (NoSuchOptionException e) {
                printer.printLine(e.getMessage());
            } catch (InputMismatchException e) {
                printer.printLine("Wprowadzono wartość, która nie jest liczbą, podaj ponownie.");
            }
        }
        return option;
    }

    private void printOptions() {
        printer.printLine("Wybierz opcję: ");
        for (Option value : Option.values()) {
            printer.printLine(value.toString());
        }

    }

    private void addBook() {
        try {
            Book book = dataReader.readAndCreateBook();
            library.addPublication(book);
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się stworzyć książki, niepoprawne dane.");
        } catch (ArrayIndexOutOfBoundsException e) {
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnej książki.");
        }
    }

    private void deleteBook() {
        try {
            Book book = dataReader.readAndCreateBook();
            if (library.removePublication(book)) {
                printer.printLine("Udało się usunąć książkę");
            } else
                printer.printLine("Brak wskazanej książki");
        } catch (InputMismatchException e) {
            printer.printLine("Niepoprawne dane");
        }
    }

    private void addMagazine() {
        try {
            Magazine magazine = dataReader.readAndCreateMagazine();
            library.addPublication(magazine);
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się stworzyć magazynu, niepoprawne dane.");
        } catch (ArrayIndexOutOfBoundsException e) {
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnego magazynu.");
        }
    }

    private void addUser() {
        LibraryUser user = dataReader.createLibraryUser();
        try {
            library.addUser(user);
        } catch (UserAlreadyExistsException e) {
            printer.printLine(e.getMessage());
        }
    }

    private void deleteMagazine() {
        try {
            Magazine magazine = dataReader.readAndCreateMagazine();
            if (library.removePublication(magazine)) {
                printer.printLine("Udało się usunąć magazyn");
            } else
                printer.printLine("Brak wskazanego magazynu");
        } catch (InputMismatchException e) {
            printer.printLine("Niepoprawne dane");
        }
    }

    private void printBooks() {
        printer.printBooks(library.getSortedPublications(
                //(m1, m2) -> m1.getTitle().compareToIgnoreCase(m2.getTitle()))
                Comparator.comparing(Publication::getTitle, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void printMagazines() {
        printer.printMagazines(library.getSortedPublications(
                //(m1, m2) -> m1.getTitle().compareToIgnoreCase(m2.getTitle()))
                Comparator.comparing(Publication::getTitle, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void printUsers() {
        printer.printUsers(library.getSortedUsers(
                //(u1, u2) -> u1.getLastName().compareToIgnoreCase(u2.getLastName()))
                Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void exit() {
        try {
            fileManager.exportData(library);
            printer.printLine("Udało się zapisać dane do pliku.");
        } catch (DataExportException e) {
            printer.printLine(e.getMessage());
        }
        printer.printLine("Koniec programu, papa!");
        dataReader.close();
    }

    private enum Option {
        EXIT(0, "wyjście z programu"),
        ADD_BOOK(1, "dodanie nowej książki"),
        ADD_MAGAZINE(2, "dodanie nowego magazynu"),
        PRINT_BOOKS(3, "wyświetl dostępne książki"),
        PRINT_MAGAZINES(4, "wyświetl dostępne magazyny"),
        DELETE_BOOK(5, "usuń książkę"),
        DELETE_MAGAZINE(6, "usuń magazyn"),
        ADD_USER(7, "dodaj czytelnika"),
        PRINT_USERS(8, "wyświetl czytelników"),
        FIND_PUBLICATION(9, "znajdź książkę");

        private final int value;
        private final String description;

        Option(int value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String toString() {
            return value + " – " + description;
        }

        static Option createFromInt(int option) throws NoSuchOptionException {
            try {
                return values()[option];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchOptionException("Brak opcji o id " + option);
            }
        }
    }
}