import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ValidationInterface {

    public boolean isValidPassword(String password) ;

    public String doHashing(String password);
}
