import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_alice {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        // ===== Public parameters =====
        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        // ===== Generate private key =====
        SecureRandom rand = new SecureRandom();
        BigInteger xa = new BigInteger(10, rand); // Alice private key

        // Compute public key: YA = g^xa mod p
        BigInteger YA = g.modPow(xa, p);

        System.out.println("\nAlice Private Key: " + xa);
        System.out.println("Alice Public Key: " + YA);

        // ===== Connect to attacker =====
        Socket socket = new Socket("localhost", 6000);

        // Use BufferedReader & PrintWriter instead of Data streams
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send Alice's public key
        out.println(YA.toString());

        // Receive fake key (attacker pretending to be Bob)
        BigInteger fakeKey = new BigInteger(in.readLine());

        System.out.println("Public key received (from attacker): " + fakeKey);

        // Compute shared key with attacker (not Bob)
        BigInteger KA = fakeKey.modPow(xa, p);

        System.out.println("Common key between Alice and Attacker: " + KA);

        socket.close();
    }
}