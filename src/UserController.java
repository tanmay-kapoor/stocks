import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController {
  public void getPassword() throws NoSuchAlgorithmException {
    String password = "fruitninjaz";

    System.out.println("Encrypted: " + encryptMD5(password));
    System.out.println("Encrypted: " + encryptSHA256(password));

  }

  void checkPassword(String password) {

  }

  private String encryptMD5(String password) throws NoSuchAlgorithmException {
    MessageDigest m = MessageDigest.getInstance("MD5");
    m.update(password.getBytes());
    byte[] bytes = m.digest();
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < bytes.length; i++) {
      s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    return s.toString();
  }

  private String encryptSHA256(String password) throws NoSuchAlgorithmException {
    /* MessageDigest instance for hashing using SHA256 */
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    /* digest() method called to calculate message digest of an input and return array of byte */
    byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

    /* Convert byte array of hash into digest */
    BigInteger number = new BigInteger(1, hash);

    /* Convert the digest into hex value */
    StringBuilder hexString = new StringBuilder(number.toString(16));

    /* Pad with leading zeros */
    while (hexString.length() < 32)
    {
      hexString.insert(0, '0');
    }

    return hexString.toString();
  }


  public static void main(String[] args) throws NoSuchAlgorithmException {
    UserController uc = new UserController();
    uc.getPassword();
  }

}
