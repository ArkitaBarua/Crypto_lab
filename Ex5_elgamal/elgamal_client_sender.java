import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class elgamal_client_sender {

    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        // ===== Step 1: Receive public parameters =====   from receiver
        // q → large prime number
        // alpha → primitive root modulo q
        // y → public key = alpha^x mod q
        BigInteger q = new BigInteger(in.readLine());
        BigInteger alpha = new BigInteger(in.readLine());
        BigInteger y = new BigInteger(in.readLine());

        System.out.println("Received Public Parameters:");
        System.out.println("q: " + q);
        System.out.println("alpha: " + alpha);
        System.out.println("y: " + y);

        // ===== Step 2: Take message input =====
        // IMPORTANT: message must be less than q
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter message (integer < q): ");
        BigInteger m = new BigInteger(sc.nextLine()); /////////

        // ===== Step 3: Generate random ephemeral key k =====
        // This value must be random for every encryption
        SecureRandom rand = new SecureRandom();

        BigInteger k;
        do {
            // Generate random number of size similar to q
            k = new BigInteger(q.bitLength(), rand); ///bitLengthhh

            // Ensure: 1 ≤ k < q
        } while (k.compareTo(BigInteger.ZERO) <= 0 || k.compareTo(q) >= 0); ///similar (k <= 0 || k >= q);

        // ===== Step 4: Encryption =====

        // C1 = alpha^k mod q
        BigInteger c1 = alpha.modPow(k, q);

        // Compute shared secret(K): y^k mod q
        // Since y = alpha^x, this becomes alpha^(kx)
        BigInteger K = y.modPow(k, q);

        // C2 = m * shared mod q
        // This hides the message using shared secret
        BigInteger c2 = m.multiply(K).mod(q);

        System.out.println("\nCipher Generated:");
        System.out.println("C1: " + c1);
        System.out.println("C2: " + c2);

        // ===== Step 5: Send ciphertext =====
        out.println(c1.toString());
        out.println(c2.toString());

        // Close connection
        s.close();
    }
}