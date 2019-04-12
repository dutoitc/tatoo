package ch.mno.tatoo.facade.karaf;

import ch.mno.tatoo.facade.karaf.commands.AbstractCommand;
import ch.mno.tatoo.facade.karaf.commands.AbstractKarafAction;
import ch.mno.tatoo.facade.karaf.data.KarafElement;

import java.util.List;

public interface KarafFacade extends AutoCloseable {

    List<KarafElement> findKarafElement() throws Exception;

    void clean(boolean dryRun, String name) throws Exception;

    String findDoubleVersions() throws Exception;

    void execute(AbstractKarafAction action) throws Exception;

    String execute(AbstractCommand command) throws Exception;

    @Override
    void close() throws Exception;
}
