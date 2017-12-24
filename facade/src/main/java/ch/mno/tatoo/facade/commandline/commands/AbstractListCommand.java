package ch.mno.tatoo.facade.commandline.commands;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by dutoitc on 11/08/15.
 */
public abstract class AbstractListCommand<T> extends AbstractCommand {

    private Logger LOG = LoggerFactory.getLogger(AbstractListCommand.class);

    private final Pattern pattern;

    public AbstractListCommand(Pattern pattern) {
        this.pattern = pattern;
    }


    /**
     * Extract a list of objects from last data received from listX on the server.
     * Rebuild path as the server return an indented form of joblist. the toplevel "Standard jobs" is not reported.
     *
     * Example:
     * <pre>
     * [Standard jobs]
     *    [A_alpha]
     *       [B_beta]
     *          JOB_Gamma
     *          JOB_Delta
     *       [E_Epsilon]
     *          JOB_Iota
     *       JOB_Lambda
     * </pre>
     * Will return jobs with path as
     * <pre>
     * [A_alpha.B_beta.JOB_Gamma, A_alpha.B_beta.JOB_Delta, A_alpha.E_Epsilon.JOB_iota, A_alpha.JOB_Lambda].
     * </pre>
     */
    public List<T> extractData() {
        String lastData = getLastData();
        List<T> values = new ArrayList<>();
        List<String> path = new ArrayList<>();
        LOG.trace("Data extraction of{}", lastData);
        for (String line : lastData.split("\n")) {
            int p = line.indexOf('[');
            if (p >= 0) {
                // Remove items from right until level (p/2)
                while (path.size() > p / 2) {
                    path.remove(path.size() - 1);
                }

                // Add this level
                path.add(line.trim().replace("[", "").replace("]", ""));

            } else {
                // Remove items from right until level (p2/2)
                int p2 = (line.length() - line.replace(" ", "").length());
                while (path.size() > p2 / 2) {
                    path.remove(path.size() - 1);
                }

                String s = line.trim();
                if (s.length() > 3) {
                    //.replace("I_IntegrationContinue.", "")
                    String join = StringUtils.join(path, ".");
                    join = join.replace("Standard Jobs.", "");
                    values.add(buildElement(s, join));
                }
            }
        }

        // Post-filter
        LOG.info("Filtering {} values with pattern \"{}\"", values.size(), pattern.pattern());
        return values.stream()
                .filter(e -> pattern.matcher(getFullPath(e)).matches() || pattern.matcher(getShortPath(e)).matches())
                .collect(Collectors.toList());
    }

    protected abstract String getFullPath(T obj);

    protected abstract String getShortPath(T obj);

    protected abstract T buildElement(String s, String join);


    public boolean isImmediate() {
        return true;
    }

}
