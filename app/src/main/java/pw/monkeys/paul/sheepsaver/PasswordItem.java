package pw.monkeys.paul.sheepsaver;

/**
 * Created by Paul on 11/19/2014.
 */
public class PasswordItem {
    private long id;
    private String domain;
    private String username;
    private String storedPassword;
    private int passwordStrength;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getPasswordStrength() {
        return passwordStrength;
    }
    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }
    public String getStoredPassword() {
        return storedPassword;
    }
    public void setStoredPassword(String storedPassword) {
        this.storedPassword = storedPassword;
    }
    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return domain;
    }
}