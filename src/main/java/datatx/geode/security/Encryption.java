package datatx.geode.security;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author PaulVermeulen
 *
 */
public class Encryption {
	private static final String MASTER = "security-encryption-master";
	private static final String CIPHER = "AES";
	private static final String ENCRYPTION = "AES";

	public static String encrypt(String plainText) throws EncryptionException {
		byte[] key = getKey();
		return encrypt(plainText, key);
	}

	/**
	 * encrypt
	 * 
	 * Encrypts a plain text password using a passphrase key
	 * 
	 * @param plainText
	 * @param key
	 * @return
	 * @throws EncryptionException
	 */
	public static String encrypt(String plainText, byte[] key) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER);
			SecretKeySpec secretKey = new SecretKeySpec(key, ENCRYPTION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return new String(Base64.getEncoder().encode(cipherText), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new EncryptionException("Failed to encrypt password.");
		}
	}

	/**
	 * decrypt
	 * 
	 * Decrypts an encrypted password using a passphrase key
	 * 
	 * @param plainText
	 * @param encryptedText
	 * @return
	 * @throws EncryptionException
	 */
	public static String decrypt(String encryptedText) throws EncryptionException {
		byte[] key = getKey();
		return decrypt(encryptedText, key);
	}

	/**
	 * decrypt
	 * 
	 * Decrypts an encrypted password using a passphrase key
	 * 
	 * @param plainText
	 * @param encryptedText
	 * @return
	 * @throws EncryptionException
	 */
	public static String decrypt(String encryptedText, byte[] key) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER);
			SecretKeySpec secretKey = new SecretKeySpec(key, ENCRYPTION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes(StandardCharsets.UTF_8));
			return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new EncryptionException("Failed to decrypt password. Exception:" + e.getMessage());
		}
	}

	private static byte[] getKey() throws EncryptionException {
		String str = System.getProperty(MASTER);
		if (str != null && str.length() > 0) {
			if (str.startsWith("file:")) {
				StringBuilder sb = new StringBuilder();
				try {
					BufferedReader br = Files.newBufferedReader(Paths.get(str.substring(5, str.length())));
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString().getBytes();
				} catch (IOException e) {
					throw new EncryptionException("Failed to get encryption master key. Exception: " + e.getMessage());
				}
			} else {
				return str.getBytes();
			}
		} else {
			throw new EncryptionException("No encryption master key found.");
		}
	}

	/**
	 * EncryptionException class
	 *
	 */
	public static class EncryptionException extends Exception {
		private static final long serialVersionUID = 1677450115135240761L;

		public EncryptionException(String msg) {
			super(msg);
		}
	}

	/**
	 * main
	 * 
	 * Main routine 
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String passwd;
		if (args != null && args.length == 2) {
			if (args[0].equalsIgnoreCase("ENCRYPT")) {
				passwd = encrypt(args[1]);
			} else if (args[0].equalsIgnoreCase("DECRYPT")) {
				passwd = decrypt(args[1]);
			} else {
				throw new RuntimeException("Invalid command; Valid commands are encrypt and decrypt");
			}
		} else {
			throw new RuntimeException("Invalid arguments; Valid arguments encrypt password or decrypt password");
		}
		System.out.println(passwd);
	}
}
