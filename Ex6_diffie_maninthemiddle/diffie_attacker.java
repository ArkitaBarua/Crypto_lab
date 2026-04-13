import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_attacker {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        // ===== Public parameters =====
        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        SecureRandom rand = new SecureRandom();

        // Attacker creates TWO private keys
        BigInteger xd1 = new BigInteger(10, rand);
        BigInteger xd2 = new BigInteger(10, rand);

        // Corresponding public keys
        BigInteger yd1 = g.modPow(xd1, p);
        BigInteger yd2 = g.modPow(xd2, p);

        System.out.println("\nAttacker Private Keys:");
        System.out.println("Xd1: " + xd1);
        System.out.println("Xd2: " + xd2);

        System.out.println("\nAttacker Public Keys:");
        System.out.println("Yd1: " + yd1);
        System.out.println("Yd2: " + yd2);

        // ===== Wait for Alice =====
        ServerSocket server = new ServerSocket(6000);
        System.out.println("\nAttacker waiting for Alice...");
        Socket alice = server.accept();

        BufferedReader inA = new BufferedReader(new InputStreamReader(alice.getInputStream()));
        PrintWriter outA = new PrintWriter(alice.getOutputStream(), true);

        // Receive Alice's public key
        BigInteger YA = new BigInteger(inA.readLine());
        System.out.println("\nReceived Alice Public Key: " + YA);

        // Send fake key to Alice
        outA.println(yd1.toString());

        // Compute shared key with Alice
        BigInteger KA = YA.modPow(xd1, p);
        System.out.println("Common key between Alice and Attacker: " + KA);

        // ===== Wait for Bob =====
        System.out.println("\nAttacker waiting for Bob...");
        Socket bob = server.accept(); //////////

        BufferedReader inB = new BufferedReader(new InputStreamReader(bob.getInputStream()));
        PrintWriter outB = new PrintWriter(bob.getOutputStream(), true);

        // Receive Bob's public key
        BigInteger YB = new BigInteger(inB.readLine());
        System.out.println("\nReceived Bob Public Key: " + YB);

        // Send fake key to Bob
        outB.println(yd2.toString());

        // Compute shared key with Bob
        BigInteger KB = YB.modPow(xd2, p);
        System.out.println("Common key between Bob and Attacker: " + KB);

        // Close connections
        alice.close();
        bob.close();
        server.close();
    }
}