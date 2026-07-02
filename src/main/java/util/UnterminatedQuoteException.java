package util;

public class UnterminatedQuoteException extends RuntimeException {

    public UnterminatedQuoteException(char quote) {
        super("unexpected EOF while looking for matching `" + quote + "`");
    }

}
