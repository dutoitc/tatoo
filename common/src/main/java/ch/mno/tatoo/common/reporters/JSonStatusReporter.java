package ch.mno.tatoo.common.reporters;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 *  A reporter which writes message as JSON.
 *  The status property is "OK" until an error is logged ("KO" then)
 */
public class JSonStatusReporter implements Reporter {

    private class Messages {
        public String status = "OK";
        public List<String> traces = new ArrayList<>();
        public List<String> info = new ArrayList<>();
        public List<String> errors = new ArrayList<>();
    }

    private Messages messages = new Messages();


    @Override
    public void showWelcomeMessage() {

    }

    @Override
    public void showGoodbyeMessage() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(messages));
    }


    @Override
    public void logTrace(String message) {
        messages.traces.add(message);
    }

    @Override
    public void logInfo(String message) {
        messages.info.add(message);
    }

    @Override
    public void logError(String message) {
        messages.errors.add(message);
        messages.status = "KO";
    }

}