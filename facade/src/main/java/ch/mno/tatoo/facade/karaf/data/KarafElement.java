package ch.mno.tatoo.facade.karaf.data;

/**
 * Something on Karaf
 * Created by dutoitc on 22/11/17.
 */
public class KarafElement {

    private String id;
    private String state;
    private String version;
    private String location;

    public KarafElement() {
    }

    public KarafElement(String id, String state, String version, String location) {
        this.id = id;
        this.state = state;
        this.version = version;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private boolean isMaven() {
        return location.startsWith("mvn:");
    }

    public boolean isBaseGroupId(String groupIdBase) {
        return location.startsWith("mvn:" + groupIdBase)  || (location.startsWith("mvn:") && location.charAt(5)=='_' && String.valueOf(location.charAt(4)).toUpperCase().equals(String.valueOf(location.charAt(4))));
    }

    @Override
    public String toString() {
        return "KarafElement{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", version='" + version + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

}
