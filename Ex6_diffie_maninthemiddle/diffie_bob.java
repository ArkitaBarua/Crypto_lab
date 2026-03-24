import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class diffie_bob {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter prime number (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        SecureRandom rand = new SecureRandom();
        BigInteger xb = new BigInteger(10, rand);

        BigInteger YB = g.modPow(xb, p);

        System.out.println("\nBob Private Key: " + xb);
        System.out.println("Bob Public Key: " + YB);

        Socket socket = new Socket("localhost", 6000);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF(YB.toString());

        BigInteger fakeKey = new BigInteger(dis.readUTF());

        System.out.println("Public key received (from attacker): " + fakeKey);

        BigInteger KB = fakeKey.modPow(xb, p);

        System.out.println("Common key between Bob and Attacker: " + KB);

        socket.close();
    }
}
