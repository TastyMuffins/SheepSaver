package pw.monkeys.paul.sheepsaver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //password strength checker
    public static int passwordStrengthMeter(String password) {
        int passwordStrengthMeter = 0;
        String STRONG_PASSWORD = "((?=.+\\d)(?=.+[a-z])(?=.+[A-Z])(?=.+[!@#$%&*()<>=+/?]).{16,32})";
        String MODERATE_PASSWORD = "((?=.*[a-z]\\S.*)(?=.*[A-Z]\\S.*)(?=.*\\d\\S.+)([!@#$%&*()<>=+/?]*)).{10,15}";
        String WEAK_PASSWORD ="(([a-zA-Z0-9-!@#$%&*()/?><]?).{6,8})";

        if (password.matches(WEAK_PASSWORD)) {
            passwordStrengthMeter=10; //Strong password if it contains lower and upper cases, numbers, characters and is longer than 16
        }else if (password.matches(MODERATE_PASSWORD)) {
            passwordStrengthMeter = 70;//Moderate password if it contains lower and upper cases, numbers, characters and is longer than 9
        }else if(password.matches(STRONG_PASSWORD)){
            passwordStrengthMeter=100;
        }else{
            passwordStrengthMeter=0;
        }
        return passwordStrengthMeter;
    }

    private static int getNumberDigits(String inString){
        if (inString.isEmpty()) {
            return 0;
        }
        int numDigits= 0;
        int length= inString.length();

        for (int i = 0; i < length; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

}

