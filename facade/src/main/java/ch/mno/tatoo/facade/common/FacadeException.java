package ch.mno.tatoo.facade.common;

/**
 * Created by dutoitc on 30/04/18.
 */
public class FacadeException extends Exception {

    public FacadeException(String message) {
        super(message);
    }

    public FacadeException(String message, Throwable cause) {
        super(message, cause);
    }
}
