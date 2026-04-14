import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class diffie_alice {

    public static void main(String[] args) throws Exception {

        SecureRandom rand = new SecureRandom();

        // ===== Connect to attacker =====
        Socket socket = new Socket("localhost", 6000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Receive p and g from attacker
        BigInteger p = new BigInteger(in.readLine());
        BigInteger g = new BigInteger(in.readLine());

        System.out.println("Received p: " + p);
        System.out.println("Received g: " + g);

        // ===== Generate private key =====
        BigInteger xa = new BigInteger(10, rand);

        // Compute public key
        BigInteger YA = g.modPow(xa, p);

        System.out.println("\nAlice Private Key: " + xa);
        System.out.println("Alice Public Key: " + YA);

        // Send public key
        out.println(YA.toString());

        // Receive fake key from attacker
        BigInteger fakeKey = new BigInteger(in.readLine());
        System.out.println("Received fake key: " + fakeKey);

        // Compute shared key
        BigInteger KA = fakeKey.modPow(xa, p);

        System.out.println("Shared Key (Alice-Attacker): " + KA);

        socket.close();
    }
}