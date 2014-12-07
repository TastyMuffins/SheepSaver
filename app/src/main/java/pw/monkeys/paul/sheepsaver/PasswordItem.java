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

        //Pattern STRONG_PASSWORD = Pattern.compile("((?=.+\\d)(?=.+[a-z])(?=.+[A-Z])(?=.+[!@#$%&*()<>=+/?]).{16,32})");
        //Pattern MODERATE_PASSWORD = Pattern.compile("((?=.+\\d)(?=.+[a-z])(?=.+[A-Z])(?=.*[!@#$%&*()<>=+/?]).{10,15})");
        //Pattern WEAK_PASSWORD = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()<>=+/?]).{6,9})");
        //Matcher m = STRONG_PASSWORD.matcher(password);


        int passwordStrengthMeter = 1;

        //int numDigits = getNumberDigits(password);

        if (password != null) {
            if (password.length() > 5) {
                //Strong password if it contains lower and upper cases, numbers, characters and is longer than 12
                if (password.length() > 15) {
                    passwordStrengthMeter=passwordStrengthMeter+10;
                }
                //if(password.length() > 9 && password.length() < 16 && password.contains("((?=.+\\d)(?=.+[a-z])(?=.+[A-Z])(?=.+[!@#$%^&*()_-=+/?]))") ){
                  //  passwordStrengthMeter=passwordStrengthMeter+7;
                //}
                //if (password.length() > 11) {
                    //passwordStrengthMeter=passwordStrengthMeter+2;// good pw length of 9
                //}
                //else if(password.length() > 5 && password.length() < 10){
                  //  passwordStrengthMeter++;// minimal pw length of 6
                //}
                //if (password.contains("!")) {
                  //  passwordStrengthMeter=passwordStrengthMeter+2;
                //}
                //if (numDigits > 0 && numDigits != password.length()) {
                  //  passwordStrengthMeter=passwordStrengthMeter+2;
                //}else{
                  //  passwordStrengthMeter++;
                //}
               // return passwordStrengthMeter;
            }
            //return passwordStrengthMeter;
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

