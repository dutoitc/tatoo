package ch.mno.tatoo.cli.command;

/**
 * Created by dutoitc on 23/11/17.
 */
public class UsageItem {

    private String command;
    private String libelle;
    private String commandKey;

    public UsageItem(String command, String libelle) {
        this.command = command;
        this.libelle = libelle;
        int p = command.indexOf(' ');
        if (p==-1) {
            commandKey = command;
        } else {
            commandKey = command.substring(0, p);
        }
    }

    public String getCommandKey() {
        return commandKey;
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
