import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {


    public boolean isValidEmail(String email) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidAadhar(String aadhar) {
        String aadharRegex = "^[0-9]{12}$";
        Pattern pattern = Pattern.compile(aadharRegex);
        Matcher matcher = pattern.matcher(aadhar);
        return matcher.matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneNumberRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


}
