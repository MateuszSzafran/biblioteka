package pl.javastart.library.io.file;

import pl.javastart.library.app.exception.NoSuchFileTypeException;
import pl.javastart.library.io.ConsolePrinter;
import pl.javastart.library.io.DataReader;

public class FileManagerBuilder {
    private ConsolePrinter printer;
    private DataReader reader;

    public FileManagerBuilder(ConsolePrinter printer, DataReader reader) {
        this.printer = printer;
        this.reader = reader;
    }

    public FileManager built() {
        printer.printLine("Wybierz format danych: ");
        FileType fileType = getFileType();
        switch (fileType) {
            case SERIAL:
                return new SerializableFileManager();
            case CSV:
                return new CsvFileManager();
            default:
                throw new NoSuchFileTypeException("Nieobsługiwany typ danych.");
        }
    }

    private FileType getFileType() {
        boolean wrongFileType = true;
        FileType fileType = null;
        do {
            printTypes();
            String type = reader.getString().toUpperCase();
            try {
                fileType = FileType.valueOf(type);
                wrongFileType = false;
            }catch (IllegalArgumentException e){
                printer.printLine("Nieobsługiwany typ danych. Wybierz ponownie.");
            }
        }while (wrongFileType);
        return fileType;
    }

    private void printTypes() {
        for (FileType value : FileType.values()) {
            printer.printLine(value.name());
        }
    }
}
