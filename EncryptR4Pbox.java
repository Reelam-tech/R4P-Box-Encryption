package javaapplication132;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class EncryptR4Pbox {

    // =========================================================
    // تحويل إلى 8 بت
    // =========================================================
    public static String toBinary8(int value) {
        String bin = Integer.toBinaryString(value);
        while (bin.length() < 8) {
            bin = "0" + bin;
        }
        return bin;
    }

    // =========================================================
    // ROTATION 4 bits
    // =========================================================
    public static int rotateLeft4(int value) {
        String bin = toBinary8(value);
        return Integer.parseInt(bin.substring(4) + bin.substring(0, 4), 2);
    }

    public static int rotateRight4(int value) {
        return rotateLeft4(value);
    }

    // =========================================================
    // P-BOX
    // =========================================================
    static int[] pbox = {2, 4, 7, 1, 6, 0, 5, 3};

    public static int applyPBox(int value) {
        String bin = toBinary8(value);
        char[] out = new char[8];
        for (int i = 0; i < 8; i++) {
            out[pbox[i]] = bin.charAt(i);
        }
        return Integer.parseInt(new String(out), 2);
    }

    public static int applyInversePBox(int value) {
        String bin = toBinary8(value);
        char[] out = new char[8];
        for (int i = 0; i < 8; i++) {
            out[i] = bin.charAt(pbox[i]);
        }
        return Integer.parseInt(new String(out), 2);
    }

    // =========================================================
    // SHA-256
    // =========================================================
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================================
    // ENCRYPT
    // =========================================================
    public static int[] encrypt(String text) {
        int[] out = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            int ascii = (int) text.charAt(i);
            int r = rotateLeft4(ascii);
            int p = applyPBox(r);
            out[i] = p;
        }
        return out;
    }

    // =========================================================
    // DECRYPT
    // =========================================================
    public static String decrypt(int[] cipher) {
        char[] out = new char[cipher.length];
        for (int i = 0; i < cipher.length; i++) {
            int invP = applyInversePBox(cipher[i]);
            int rec = rotateRight4(invP);
            out[i] = (char) rec;
        }
        return new String(out);
    }

    // =========================================================
    // MAIN (Two Phase Process)
    // =========================================================
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // ================= PHASE 1 — ENCRYPT =================
        System.out.print("Enter your name: ");
        String name = in.nextLine();

        System.out.print("\nPlaintext (in bits): ");
        for (int i = 0; i < name.length(); i++) {
            int ascii = (int) name.charAt(i);
            System.out.print(toBinary8(ascii) + " ");
        }
        System.out.println();

        int[] encrypted = encrypt(name);

        System.out.println("\n--- ENCRYPTION OUTPUT ---");
        System.out.print("Ciphertext (bits): ");

        StringBuilder cipherForHash = new StringBuilder();
        for (int v : encrypted) {
            String bits = toBinary8(v);
            System.out.print(bits + " ");
            cipherForHash.append(bits).append("-");
        }

        String hash = hashSHA256(cipherForHash.toString());
        System.out.println("\nSHA-256 Hash: " + hash);
        System.out.println("\nCopy these values for verification phase!");
        System.out.println("Ciphertext string: " + cipherForHash);
        System.out.println("Hash: " + hash);

        // ================= PHASE 2 — VERIFY + DECRYPT =================
        System.out.println("\n--- VERIFICATION PHASE ---");
        System.out.println("Paste the ciphertext (bits) exactly as shown (with -): ");
        String cipherInput = in.nextLine();

        System.out.print("Enter the hash: ");
        String userHash = in.nextLine();

        String newHash = hashSHA256(cipherInput);
        System.out.println("\nRecalculated Hash: " + newHash);

        if (newHash.equals(userHash)) {
            System.out.println("Integrity: OK (Hash matches)");
        } else {
            System.out.println("Integrity: FAILED (Hash mismatch!)");
        }

        String[] bitBlocks = cipherInput.split("-");
        int[] cipherArray = new int[bitBlocks.length];
        for (int i = 0; i < bitBlocks.length; i++) {
            if (bitBlocks[i].isEmpty()) continue;
            cipherArray[i] = Integer.parseInt(bitBlocks[i], 2);
        }

        String recovered = decrypt(cipherArray);
        System.out.println("Recovered plaintext: " + recovered);

        in.close();
    }
}
