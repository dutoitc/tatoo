package ch.mno.tatoo.common.reporters;

/**
 * Base for many logger types
 */
public interface Reporter {

    void showWelcomeMessage();
    void showGoodbyeMessage();
    void logTrace(String s);
    void logInfo(String message);
    void logError(String message);

}
