package ch.mno.tatoo.facade.bonita;

import org.bonitasoft.engine.bpm.flownode.FlowNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class BonitaStatsResult {

    protected List<BonitaStatsResultElement> data = new ArrayList();

    public List<BonitaStatsResultElement> getData() {
        return data;
    }

    public void add(FlowNodeType type, String name, String state) {
        data.add(new BonitaStatsResultElement(type, name, state));
    }

    public long count(FlowNodeType type) {
        return data.stream().filter(a->type.equals(a.type)).count();
    }

    public long countExecutingNotHuman() {
        Stream<BonitaStatsResultElement> cases = data.stream().filter(a -> "executing".equals(a.state) && a.getType() != FlowNodeType.HUMAN_TASK && a.getType() != FlowNodeType.USER_TASK);
        return cases.count();
    }

    /**
     *
     * @param type
     * @param state ready, executing, completed, failed
     * @return
     */
    public long count(FlowNodeType type, String state) {
        return data.stream().filter(a->type.equals(a.type)).filter(a->state.equals(a.state)).count();
    }


    class BonitaStatsResultElement {
        private FlowNodeType type;
        private String name;
        private String state;

        public FlowNodeType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public BonitaStatsResultElement(FlowNodeType type, String name, String state) {
            this.type = type;
            this.name = name;
            this.state = state;
        }
    }

}