package emotionalsongs.common;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contiene metodi utili alla formattazione di stringhe
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class StringHelpers {

    private StringHelpers() {

    }

    /**
     * Crea una stringa alfabetica a partire da un intero
     *
     * @param i l'intero da convertire
     * @return la stringa alfabetica
     */
    public static String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ('A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    /**
     * Testa l'invalidità di una stringa con una espressione regolare
     *
     * @param regEx la stringa regEx
     * @param text  la stringa da testare
     * @return true se la stringa è invalida
     */
    public static boolean invalidRegExMatch(String regEx, String text) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);

        return !m.find();
    }

    /**
     * Formatta la durata totale di una lista di canzoni
     *
     * @param songsList la lista di canzoni
     * @return la stringa nel formato 'h ore, m minuti, s secondi'
     */
    public static String getSongsListDurationString(List<Song> songsList) {
        Integer duration = 0;

        for (Song song : songsList) {
            duration += song.getDurationInt();
        }

        return duration == 0 ? "0" :
                String.format("%d ore, %02d minuti, %02d secondi", duration / 3600, (duration % 3600) / 60, (duration % 60));
    }

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

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
