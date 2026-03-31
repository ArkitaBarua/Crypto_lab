import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class elgamal_server_receiver {

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(5000);
        System.out.println("Receiver running...");

        SecureRandom rand = new SecureRandom();

        // ===== Step 1: Key Generation =====

        BigInteger q = BigInteger.probablePrime(16, rand); ////16
        BigInteger alpha = findPrimitiveRoot(q); 
        BigInteger x = new BigInteger(q.bitLength() - 1, rand);

        // Public key y = alpha^x mod q
        BigInteger y = alpha.modPow(x, q);

        System.out.println("Public Parameters:");
        System.out.println("q: " + q);
        System.out.println("alpha: " + alpha);
        System.out.println("y: " + y);
        System.out.println("Private key x: " + x);

        Socket s = ss.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        // ===== Step 2: Send public parameters =====
        out.println(q.toString());
        out.println(alpha.toString());
        out.println(y.toString());

        // ===== Step 3: Receive ciphertext =====
        BigInteger c1 = new BigInteger(in.readLine());
        BigInteger c2 = new BigInteger(in.readLine());

        System.out.println("\nReceived Cipher:");
        System.out.println("C1: " + c1);
        System.out.println("C2: " + c2);

        // ===== Step 4: Decryption =====

        // Compute shared secret s = c1^x mod q
        // Since c1 = alpha^k → s = alpha^(kx)
        BigInteger sKey = c1.modPow(x, q);

        // Compute modular inverse of s
        BigInteger sInv = sKey.modInverse(q);

        // Recover message: m = c2 * s^{-1} mod q
        BigInteger message = c2.multiply(sInv).mod(q);

        System.out.println("\nDecrypted Message: " + message);

        s.close();
        ss.close();
    }

    // ===== Primitive Root Finder =====
    static BigInteger findPrimitiveRoot(BigInteger p) {

        // phi = p - 1 bec prime
        BigInteger phi = p.subtract(BigInteger.ONE);

        // Try values starting from 2
        for (BigInteger g = BigInteger.TWO; g.compareTo(p) < 0; g = g.add(BigInteger.ONE)) {

            // Weak condition: g^phi mod p = 1
            // (This is always true for valid numbers mod p)
            if (g.modPow(phi, p).equals(BigInteger.ONE))
                return g;
        }

        return BigInteger.TWO;
    }
}