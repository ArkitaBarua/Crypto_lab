import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.BigInteger;
import java.security.MessageDigest;

public class receiver_dss {

    public static void main(String[] args) throws Exception {

        System.out.println("===== DSS RECEIVER =====");
        System.out.println("Server waiting on port 5000...\n");

        ServerSocket server = new ServerSocket(5000);
        Socket connection = server.accept();

        ObjectInputStream input = new ObjectInputStream(connection.getInputStream());

        System.out.println("Client connected!\n");

        // Receive all components
        BigInteger p = (BigInteger) input.readObject();
        BigInteger q = (BigInteger) input.readObject();
        BigInteger g = (BigInteger) input.readObject();
        BigInteger y = (BigInteger) input.readObject();
        String msg = (String) input.readObject();
        BigInteger r = (BigInteger) input.readObject();
        BigInteger s = (BigInteger) input.readObject();

        System.out.println("Received from Sender: p,q,g,y,r,s,Message");
        System.out.println("Message: " + msg);

        // Verification steps
        boolean result = verifySignature(p, q, g, y, msg, r, s);

        if (result) {
            System.out.println("\nSuccessful Verified Signature");
        } else {
            System.out.println("\nIncorrect Signature !!!");
        }

        input.close();
        connection.close();
        server.close();
    }

    public static boolean verifySignature(BigInteger p, BigInteger q, BigInteger g,
                                          BigInteger y, String msg,
                                          BigInteger r, BigInteger s) throws Exception {

        // Step 1: Check bounds
        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0 ||
            s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
            return false;
        }

        // Step 2: Hash
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(1, md.digest(msg.getBytes()));

        // Step 3: w = s^-1 mod q
        BigInteger w = s.modInverse(q);

        // Step 4: u1 and u2
        BigInteger u1 = hash.multiply(w).mod(q);
        BigInteger u2 = r.multiply(w).mod(q);

        System.out.println("\nVerification:");
        System.out.println("w: " + w);
        System.out.println("u1: " + u1);
        System.out.println("u2: " + u2);

        // Step 5: v calculation
        BigInteger part1 = g.modPow(u1, p);
        BigInteger part2 = y.modPow(u2, p);
        BigInteger v = part1.multiply(part2).mod(p).mod(q);

        System.out.println("v: " + v);
        System.out.println("\nr' (from Sender): " + r);
        System.out.println("v  (Calculated): " + v);

        return v.equals(r);
    }
}