import java.io.ObjectOutputStream;
import java.net.Socket;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Scanner;

public class sender_DSS {

    public static void main(String[] args) throws Exception {

        SecureRandom rand = new SecureRandom();
        Scanner sc = new Scanner(System.in);

        System.out.println("===== DSS SENDER =====");

        // Generate q
        BigInteger q = BigInteger.probablePrime(160, rand);

        // Generate p
        BigInteger p, temp;
        do {
            temp = new BigInteger(352, rand);
            p = temp.multiply(q).add(BigInteger.ONE);
        } while (!p.isProbablePrime(50));

        // Generate g
        BigInteger g, h;
        do {
            h = new BigInteger(350, rand);
            g = h.modPow((p.subtract(BigInteger.ONE)).divide(q), p);
        } while (g.compareTo(BigInteger.ONE) <= 0);

        System.out.println("\nGlobal Public Key Components:");
        System.out.println("p: " + p);
        System.out.println("q: " + q);
        System.out.println("g: " + g);

        // Keys
        BigInteger x = new BigInteger(159, rand); // private
        BigInteger y = g.modPow(x, p);            // public

        System.out.println("\nUser's Private Key (x): " + x);
        System.out.println("User's Public Key (y): " + y);

        // Input message
        System.out.print("\nEnter the message: ");
        String message = sc.nextLine();

        // Hash
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(1, md.digest(message.getBytes()));
        System.out.println("Hash Value (SHA-256): " + hash);

        // Choice for invalid test case
        System.out.print("\nDo you want to send the correct message? (y/n): ");
        char choice = sc.next().charAt(0);

        if (choice == 'n') {
            x = x.add(BigInteger.TWO); // deliberately corrupt
        }

        // Signature
        BigInteger[] sig = generateSignature(message, p, q, g, x);
        BigInteger r = sig[0];
        BigInteger s = sig[1];

        System.out.println("\nSignature: [r: " + r + ", s: " + s + "]");

        // Send data
        Socket socket = new Socket("localhost", 5000);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

        System.out.println("\nSending to client: p,q,g,y,r,s,Message");

        output.writeObject(p);
        output.writeObject(q);
        output.writeObject(g);
        output.writeObject(y);
        output.writeObject(message);
        output.writeObject(r);
        output.writeObject(s);

        System.out.println("Sent Successfully");

        output.close();
        socket.close();
    }

    public static BigInteger[] generateSignature(String msg, BigInteger p, BigInteger q,
                                                 BigInteger g, BigInteger x) throws Exception {

        SecureRandom rand = new SecureRandom();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(1, md.digest(msg.getBytes()));

        BigInteger k, r, s;

        do {
            k = new BigInteger(159, rand);

            r = g.modPow(k, p).mod(q);

            BigInteger kInv = k.modInverse(q);
            s = (kInv.multiply(hash.add(x.multiply(r)))).mod(q);

        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

        return new BigInteger[]{r, s};
    }
}