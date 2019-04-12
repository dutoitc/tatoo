package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.common.reporters.Reporter;
import ch.mno.tatoo.facade.bonita.BonitaFacade;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.karaf.KarafSSHFacade;
import ch.mno.tatoo.facade.tac.TACFacade;

import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public abstract class AbstractCommand {

    protected RuntimeProperties properties;
    protected Reporter reporter;

    /** Called after bean initialization */
    protected void postInit() {

    }

    protected TACFacade buildTACFacade() {
        String tacUrl = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_URL);
        String tacUsername = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_USERNAME);
        String tacPassword = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_PASSWORD);
        String tacEmail = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_EMAIL);
        //
        return new TACFacade(tacUrl, tacUsername, tacPassword, tacEmail);
    }


    protected KarafFacade buildKarafFacade() {
        String karafHostname = properties.get(RuntimeProperties.PROPERTIES.KARAF_HOSTNAME);
        int karafPort = Integer.parseInt(properties.get(RuntimeProperties.PROPERTIES.KARAF_PORT));
        String karafUsername = properties.get(RuntimeProperties.PROPERTIES.KARAF_USERNAME);
        String karafPassword = properties.get(RuntimeProperties.PROPERTIES.KARAF_PASSWORD);
        return new KarafSSHFacade(karafHostname, karafPort, karafUsername, karafPassword);
    }

    protected BonitaFacade buildBonitaFacade() {
        String bonitaURL = properties.get(RuntimeProperties.PROPERTIES.BONITA_URL);
        String bonitaContext = properties.get(RuntimeProperties.PROPERTIES.BONITA_CONTEXT);
        String bonitaUser = properties.get(RuntimeProperties.PROPERTIES.BONITA_USER);
        String bonitaPassword = properties.get(RuntimeProperties.PROPERTIES.BONITA_PASSWORD);
        return new BonitaFacade(bonitaURL, bonitaContext, bonitaUser, bonitaPassword);
    }


    public void setProperties(RuntimeProperties properties) {
        this.properties = properties;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }


    /** Return usage */
    public abstract List<UsageItem> getUsage();


    public boolean canHandle(List<String> args) {
        if (args.size()<1) return false;
        String key = args.get(0);
        return getUsage().stream().filter(u->u.getCommandKey().equals(key)).findAny().isPresent();
    }


    public abstract void handle(List<String> args);
}
