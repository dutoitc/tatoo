package ch.mno.tatoo.common.reporters;

/**
 * A reporter which writes message to console.
 */
public class ConsoleReporter implements Reporter {

    @Override
    public void showWelcomeMessage() {
        System.out.println(".========================================");
        System.out.println();
    }

    @Override
    public void showGoodbyeMessage() {
        System.out.println("");
        System.out.println("Have a nice day !");
    }

    @Override
    public void logTrace(String message) {
        System.out.println("TRACE: " + message);
    }


    @Override
    public void logInfo(String message) {
        System.out.println("INFO:  " + message);
        System.out.flush();
    }

    @Override
    public void logError(String message){
        System.err.println("ERR:   " + message);
        System.err.flush();
    }

}
