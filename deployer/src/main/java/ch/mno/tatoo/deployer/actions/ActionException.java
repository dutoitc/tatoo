package ch.mno.tatoo.deployer.actions;

/**
 * Created by dutoitc on 03/10/17.
 */
public class ActionException extends Exception {

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
