import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class rsa_23bce1099 {

    public static void main(String[] args) {

        Scanner a = new Scanner(System.in);
        SecureRandom random = new SecureRandom();

        BigInteger p = BigInteger.probablePrime(1024, random);
        BigInteger q = BigInteger.probablePrime(1024, random);

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(65537);
        BigInteger d = e.modInverse(phi);

        System.out.print("plaintext: ");
        String message = a.nextLine();

        BigInteger messageInt = new BigInteger(message.getBytes());

        BigInteger enc = messageInt.modPow(e, n);
        BigInteger dec = enc.modPow(d, n);

        String decm = new String(dec.toByteArray());

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