import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_attacker {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        SecureRandom rand = new SecureRandom();

        BigInteger xd1 = new BigInteger(10, rand);
        BigInteger xd2 = new BigInteger(10, rand);

        BigInteger yd1 = g.modPow(xd1, p);
        BigInteger yd2 = g.modPow(xd2, p);

        System.out.println("\nAttacker Private Keys:");
        System.out.println("Xd1: " + xd1);
        System.out.println("Xd2: " + xd2);

        System.out.println("\nAttacker Public Keys:");
        System.out.println("Yd1: " + yd1);
        System.out.println("Yd2: " + yd2);

        ServerSocket server = new ServerSocket(6000);
        System.out.println("\nAttacker waiting for Alice...");
        Socket alice = server.accept();

        DataInputStream disA = new DataInputStream(alice.getInputStream());
        DataOutputStream dosA = new DataOutputStream(alice.getOutputStream());

        BigInteger YA = new BigInteger(disA.readUTF());
        System.out.println("\nReceived Alice Public Key: " + YA);

        dosA.writeUTF(yd1.toString());  // send fake key

        BigInteger KA = YA.modPow(xd1, p);
        System.out.println("Common key between Alice and Attacker: " + KA);

        System.out.println("\nAttacker waiting for Bob...");
        Socket bob = server.accept();

        DataInputStream disB = new DataInputStream(bob.getInputStream());
        DataOutputStream dosB = new DataOutputStream(bob.getOutputStream());

        BigInteger YB = new BigInteger(disB.readUTF());
        System.out.println("\nReceived Bob Public Key: " + YB);

        dosB.writeUTF(yd2.toString());  // send fake key

        BigInteger KB = YB.modPow(xd2, p);
        System.out.println("Common key between Bob and Attacker: " + KB);

        alice.close();
        bob.close();
        server.close();
    }
}
