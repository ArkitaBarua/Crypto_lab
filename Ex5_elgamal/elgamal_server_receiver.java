import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class elgamal_server_receiver {

    public static void main(String[] args) {

        int port = 5000;

        try (ServerSocket ss = new ServerSocket(port)) {

            System.out.println("Receiver running on port " + port);

            // ===== Key Generation =====
            SecureRandom rand = new SecureRandom();

            BigInteger q = BigInteger.probablePrime(16, rand); // small prime for demo
            BigInteger alpha = findPrimitiveRoot(q);
            BigInteger x = new BigInteger(8, rand); // private key
            BigInteger y = alpha.modPow(x, q); // public key

            System.out.println("Public Parameters:");
            System.out.println("Prime (q): " + q);
            System.out.println("Primitive Root (alpha): " + alpha);
            System.out.println("Public Key (y): " + y);
            System.out.println("Private Key (x): " + x);

            Socket s = ss.accept();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Send public parameters to client
            dos.writeUTF(q.toString());
            dos.writeUTF(alpha.toString());
            dos.writeUTF(y.toString());

            // Receive ciphertext
            BigInteger c1 = new BigInteger(dis.readUTF());
            BigInteger c2 = new BigInteger(dis.readUTF());

            System.out.println("\nReceived Cipher:");
            System.out.println("C1: " + c1);
            System.out.println("C2: " + c2);

            // ===== Decryption =====
            BigInteger sKey = c1.modPow(x, q);
            BigInteger sInv = sKey.modInverse(q);
            BigInteger message = c2.multiply(sInv).mod(q);

            System.out.println("\nDecrypted Message: " + message);

            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Simple primitive root finder (demo version)
    static BigInteger findPrimitiveRoot(BigInteger p) {
        BigInteger phi = p.subtract(BigInteger.ONE);
        for (BigInteger g = BigInteger.TWO;
             g.compareTo(p) < 0;
             g = g.add(BigInteger.ONE)) {

            if (g.modPow(phi, p).equals(BigInteger.ONE))
                return g;
        }
        return BigInteger.TWO;
    }
}
