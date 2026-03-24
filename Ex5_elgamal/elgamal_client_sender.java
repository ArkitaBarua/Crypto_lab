import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class elgamal_client_sender {

    public static void main(String[] args) {

        try (Socket s = new Socket("localhost", 5000)) {

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Receive public parameters
            BigInteger q = new BigInteger(dis.readUTF());
            BigInteger alpha = new BigInteger(dis.readUTF());
            BigInteger y = new BigInteger(dis.readUTF());

            System.out.println("Received Public Parameters:");
            System.out.println("q: " + q);
            System.out.println("alpha: " + alpha);
            System.out.println("y: " + y);

            Scanner sc = new Scanner(System.in);
            System.out.print("Enter message (integer < q): ");
            BigInteger m = new BigInteger(sc.nextLine());

            SecureRandom rand = new SecureRandom();
            BigInteger k = new BigInteger(8, rand);

            // ===== Encryption =====
            BigInteger c1 = alpha.modPow(k, q);
            BigInteger c2 = m.multiply(y.modPow(k, q)).mod(q);

            System.out.println("\nCipher Generated:");
            System.out.println("C1: " + c1);
            System.out.println("C2: " + c2);

            // Send ciphertext
            dos.writeUTF(c1.toString());
            dos.writeUTF(c2.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
