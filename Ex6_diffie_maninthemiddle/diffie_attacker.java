import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class diffie_attacker {

    // ===== Primitive Root Finder =====
    static BigInteger findPrimitiveRoot(BigInteger p) {

    // phi = p - 1 (since p is prime, Euler's totient function φ(p) = p - 1)
    BigInteger phi = p.subtract(BigInteger.ONE);

    // Try all possible values of g starting from 2
    for (BigInteger g = BigInteger.TWO; 
         g.compareTo(p) < 0; 
         g = g.add(BigInteger.ONE)) {

        // Assume current g is a primitive root
        boolean isPrimitive = true;

        // Copy of phi for factorization (so original phi is not modified)
        BigInteger tempPhi = phi;

        // Find prime factors of phi using brute force
        for (BigInteger i = BigInteger.TWO; 
             i.multiply(i).compareTo(tempPhi) <= 0; 
             i = i.add(BigInteger.ONE)) {

            // If i divides phi, then i is a factor
            if (tempPhi.mod(i).equals(BigInteger.ZERO)) {

                // Check primitive root condition:
                // g^(phi / i) mod p should NOT be 1
                // If it is 1 → g is NOT a primitive root
                if (g.modPow(phi.divide(i), p).equals(BigInteger.ONE)) {
                    isPrimitive = false;
                    break;
                }

                // Remove all occurrences of factor i from tempPhi
                // (to ensure we only check distinct prime factors)
                while (tempPhi.mod(i).equals(BigInteger.ZERO)) {
                    tempPhi = tempPhi.divide(i);
                }
            }
        }

        // If tempPhi is still > 1, it is a remaining prime factor
        if (tempPhi.compareTo(BigInteger.ONE) > 0) {

            // Again check primitive root condition for this factor
            if (g.modPow(phi.divide(tempPhi), p).equals(BigInteger.ONE)) {
                isPrimitive = false;
            }
        }

        // If g passed all checks, it is a valid primitive root
        if (isPrimitive) {
            return g;
        }
    }

    // Fallback (should never happen for valid prime p)
    return BigInteger.TWO;
}

    public static void main(String[] args) throws Exception {

        SecureRandom rand = new SecureRandom();

        // ===== Generate prime p =====
        BigInteger p = BigInteger.probablePrime(16, rand); // small for demo

        // ===== Find primitive root g =====
        BigInteger g = findPrimitiveRoot(p);

        System.out.println("Generated Prime p: " + p);
        System.out.println("Primitive Root g: " + g);

        // ===== Attacker private keys =====
        BigInteger xd1 = new BigInteger(10, rand);
        BigInteger xd2 = new BigInteger(10, rand);

        // Public keys
        BigInteger yd1 = g.modPow(xd1, p);
        BigInteger yd2 = g.modPow(xd2, p);

        System.out.println("\nAttacker Private Keys:");
        System.out.println("Xd1: " + xd1);
        System.out.println("Xd2: " + xd2);

        System.out.println("\nAttacker Public Keys:");
        System.out.println("Yd1: " + yd1);
        System.out.println("Yd2: " + yd2);

        // ===== Start server =====
        ServerSocket server = new ServerSocket(6000);

        // ==================== ALICE ====================
        System.out.println("\nWaiting for Alice...");
        Socket alice = server.accept();

        BufferedReader inA = new BufferedReader(new InputStreamReader(alice.getInputStream()));
        PrintWriter outA = new PrintWriter(alice.getOutputStream(), true);

        // Send p and g
        outA.println(p.toString());
        outA.println(g.toString());

        // Receive Alice public key
        BigInteger YA = new BigInteger(inA.readLine());
        System.out.println("\nReceived Alice Public Key: " + YA);

        // Send fake key to Alice
        outA.println(yd1.toString());

        // Compute shared key with Alice
        BigInteger KA = YA.modPow(xd1, p);
        System.out.println("Common key between Alice and Attacker: " + KA);

        // ==================== BOB ====================
        System.out.println("\nWaiting for Bob...");
        Socket bob = server.accept();

        BufferedReader inB = new BufferedReader(new InputStreamReader(bob.getInputStream()));
        PrintWriter outB = new PrintWriter(bob.getOutputStream(), true);

        // Send p and g
        outB.println(p.toString());
        outB.println(g.toString());

        // Receive Bob public key
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