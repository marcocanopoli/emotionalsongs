package emotionalsongs.common;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Contiene metodi utili all'encrypting di stringhe.
 * Utilizzato nello specifico per l'hashing delle password
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class PasswordEncrypter {

    private PasswordEncrypter() {

    }

    /**
     * Cripta una password utilizzando l'algoritmo
     * {@link <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory>}PBKDF2WithHmacSHA1</a>
     * fornito da Java e la restituisce in una stringa esadecimale contenente il numero di iterazioni, il salt e l'hash.
     * Si è scelto di utilizzare un ciclo di 1000 iterazioni per mantenere un tempo di criptazione ragionevole senza
     * impattare l'esperienza dell'utente e allo stesso tempo scoraggiare eventuali attacchi brute force.
     *
     * @param password la password da criptare
     * @return la sttringa contenente la password criptata
     * @throws NoSuchAlgorithmException se l'algoritmo di encrypting non è disponibile
     * @throws InvalidKeySpecException  se la specifica della chiave non è valida
     */
    public static String encryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        int iterations = 1000;
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Valida una password confrontandola con la password criptata
     *
     * @param pwd       la password in chiaro
     * @param hashedPwd la password criptata
     * @return true se la password corrisponde, false altrimenti
     * @throws NoSuchAlgorithmException se l'algoritmo di encrypting non è disponibile
     * @throws InvalidKeySpecException  se la specifica della chiave non è valida
     */
    public static boolean validatePassword(String pwd, String hashedPwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hashedPwd.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(pwd.toCharArray(),
                salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    /**
     * Converte un array di byte in una stringa esadecimale
     *
     * @param array l'array di byte
     * @return la stringa esadecimale
     */
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * Converte una stringa esadecimale in un array di byte
     *
     * @param hex la stringa esadecimale
     * @return l'array di byte
     */
    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
