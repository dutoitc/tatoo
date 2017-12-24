package ch.mno.tatoo.builder.actions;

import ch.mno.tatoo.builder.Context;
import ch.mno.tatoo.builder.Report;
import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;

/**
 * Created by dutoitc on 03/10/17.
 */
public abstract class AbstractBuilder<O> {

    protected Context context;
    protected String publishedVersion;
    protected TalendCommandLineWrapper cmdline;
    protected Report report;

    public AbstractBuilder(Context context, String publishedVersion, TalendCommandLineWrapper cmdline) {
        this.context = context;
        this.publishedVersion = publishedVersion;
        this.cmdline = cmdline;
        this.report = report;
    }

    public abstract void execute(O obj, Report report);
}
