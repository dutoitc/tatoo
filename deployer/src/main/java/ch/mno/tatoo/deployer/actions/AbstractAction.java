package ch.mno.tatoo.deployer.actions;

import ch.mno.tatoo.deployer.Report;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.tac.TACFacade;

/**
 * Created by dutoitc on 03/10/17.
 */
public abstract class AbstractAction<O> {

    protected TACFacade tacFacade;
    protected KarafFacade karafFacade;
    protected boolean isDryRun;

    public AbstractAction(TACFacade tacFacade, KarafFacade karafFacade, boolean isDryRun) {
        this.tacFacade = tacFacade;
        this.karafFacade = karafFacade;
        this.isDryRun = isDryRun;
    }

    public abstract void execute(O obj, Report report) throws ActionException;
}
