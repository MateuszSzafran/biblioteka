package pl.javastart.library.app.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String msg){
        super(msg);
    }
}
