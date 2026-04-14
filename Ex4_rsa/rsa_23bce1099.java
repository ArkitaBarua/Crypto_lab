import java.math.BigInteger;
import java.security.SecureRandom; ////
import java.util.Scanner;

public class rsa_23bce1099 {

    public static void main(String[] args) {

        Scanner a = new Scanner(System.in);

        // for cryptographic randomness
        SecureRandom random = new SecureRandom();

        // Step 1: Generate two large prime numbers (1024 bits each)
        BigInteger p = BigInteger.probablePrime(1024, random);
        BigInteger q = BigInteger.probablePrime(1024, random);

        // Step 2: Compute modulus n = p * q
        // n is part of the public key
        BigInteger n = p.multiply(q);

        // Step 3: Compute Euler's Totient Function φ(n) = (p-1)*(q-1)
        BigInteger phi = p.subtract(BigInteger.ONE)
                          .multiply(q.subtract(BigInteger.ONE));

        // Step 4: Choose public exponent e
        BigInteger e;

        do {
            e = new BigInteger(16, random); // small random e
        } while (!phi.gcd(e).equals(BigInteger.ONE) 
                || e.compareTo(BigInteger.ONE) <= 0 
                || e.compareTo(phi) >= 0);

        // Step 5: Compute private key d such that:
        // d ≡ e⁻¹ mod φ(n)
        BigInteger d = e.modInverse(phi);

        System.out.print("plaintext: ");
        String message = a.nextLine();

        // Convert plaintext string into BigInteger
        BigInteger messageInt = new BigInteger(message.getBytes()); ////////////

        // Step 6: Encryption
        // ciphertext = message^e mod n
        BigInteger enc = messageInt.modPow(e, n);

        // Step 7: Decryption
        // plaintext = ciphertext^d mod n
        BigInteger dec = enc.modPow(d, n);

        // Convert decrypted BigInteger back to string
        String decm = new String(dec.toByteArray());

        // Display all values (for learning/debugging purposes)
        System.out.println("p:\n" + p);
        System.out.println("q:\n" + q);
        System.out.println("n = p * q:\n" + n);
        System.out.println("phi(n):\n" + phi);
        System.out.println("e:\n" + e);
        System.out.println("d:\n" + d);
        System.out.println("Encrypted (ciphertext):\n" + enc);
        System.out.println("Decrypted text (plaintext):\n" + decm);

    }
}