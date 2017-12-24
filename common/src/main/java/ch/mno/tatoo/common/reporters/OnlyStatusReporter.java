package ch.mno.tatoo.common.reporters;

/**
 * A Reporter which writes "OK" or "KO ..." with errors appended with a ';' separator.
 */
public class OnlyStatusReporter implements Reporter {

    public StringBuilder errors = new StringBuilder();

    @Override
    public void showWelcomeMessage() {
    }

    @Override
    public void showGoodbyeMessage() {
        if (errors.length() == 0) {
            System.out.println("OK");
        } else {
            System.out.println("KO " + errors.toString());
        }
    }

    @Override
    public void logTrace(String message) {
    }

    @Override
    public void logInfo(String message) {
    }

    @Override
    public void logError(String message) {
        errors.append(message).append(';');
    }

}