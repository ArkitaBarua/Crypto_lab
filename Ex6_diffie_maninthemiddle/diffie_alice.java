import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_alice {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        SecureRandom rand = new SecureRandom();
        BigInteger xa = new BigInteger(10, rand);

        BigInteger YA = g.modPow(xa, p);

        System.out.println("\nAlice Private Key: " + xa);
        System.out.println("Alice Public Key: " + YA);

        Socket socket = new Socket("localhost", 6000);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF(YA.toString());

        BigInteger fakeKey = new BigInteger(dis.readUTF());

        System.out.println("Public key received (from attacker): " + fakeKey);

        BigInteger KA = fakeKey.modPow(xa, p);

        System.out.println("Common key between Alice and Attacker: " + KA);

        socket.close();
    }
}
