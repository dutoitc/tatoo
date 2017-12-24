package ch.mno.tatoo.cli.command;

/**
 * Created by dutoitc on 23/11/17.
 */
public class UsageItem {

    private String command;
    private String libelle;

    public UsageItem(String command, String libelle) {
        this.command = command;
        this.libelle = libelle;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

}
