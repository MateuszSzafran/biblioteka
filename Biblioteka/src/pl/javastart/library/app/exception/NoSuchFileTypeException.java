package pl.javastart.library.app.exception;

public class NoSuchFileTypeException extends RuntimeException {
    public NoSuchFileTypeException(String message){
        super(message);
    }
}
