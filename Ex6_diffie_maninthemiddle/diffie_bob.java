import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_bob {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        // ===== Public parameters =====
        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        // ===== Generate private key =====
        SecureRandom rand = new SecureRandom();
        BigInteger xb = new BigInteger(10, rand);

        // Compute public key
        BigInteger YB = g.modPow(xb, p);

        System.out.println("\nBob Private Key: " + xb);
        System.out.println("Bob Public Key: " + YB);

        // ===== Connect to attacker =====
        Socket socket = new Socket("localhost", 6000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send Bob's public key
        out.println(YB.toString());

        // Receive fake key (attacker pretending to be Alice)
        BigInteger fakeKey = new BigInteger(in.readLine());

        System.out.println("Public key received (from attacker): " + fakeKey);

        // Compute shared key with attacker
        BigInteger KB = fakeKey.modPow(xb, p);

        System.out.println("Common key between Bob and Attacker: " + KB);

        socket.close();
    }
}