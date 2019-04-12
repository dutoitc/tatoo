package ch.mno.tatoo.facade.karaf.data;

public class KarafBundle {

    private String bundleId;
    private String name;
    private boolean fragment;
    private String state;
    private String version;
    private String symbolicName;

    /** mvn:ch.mno.tatoo.service.E_Traitement.A_Commun/BuildAndStoreFacebook-control-bundle/88.32.30" */
    private String bundleLocation;

    /** Wed Nov 14 17:45:35 CET 2018 */
    private String lastModification;
    private String importedPackages;
    private String exportedPackages;

    public KarafBundle(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleId() {
        return bundleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFragment() {
        return fragment;
    }

    public void setFragment(boolean fragment) {
        this.fragment = fragment;
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

    public String getSymbolicName() {
        return symbolicName;
    }

    public void setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    public String getBundleLocation() {
        return bundleLocation;
    }

    public void setBundleLocation(String bundleLocation) {
        this.bundleLocation = bundleLocation;
    }

    public String getLastModification() {
        return lastModification;
    }

    public void setLastModification(String lastModification) {
        this.lastModification = lastModification;
    }

    public void setImportedPackages(String importedPackages) {
        this.importedPackages = importedPackages;
    }

    public String getImportedPackages() {
        return importedPackages;
    }

    public void setExportedPackages(String exportedPackages) {
        this.exportedPackages = exportedPackages;
    }

    public String getExportedPackages() {
        return exportedPackages;
    }

    @Override
    public String toString() {
        return "KarafBundle{" +
                "bundleId='" + bundleId + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
