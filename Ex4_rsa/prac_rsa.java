import java.util.*;
import java.math.BigInteger;
import java.math.BigInteger.*;
import java.security.SecureRandom;
import java.security.SecureRandom.*;


public class prac_rsa {
    public static void main(String args[]){
        Scanner a = new Scanner(System.in);
        SecureRandom random= new SecureRandom();

        BigInteger p = BigInteger.probablePrime(1024, random);
        BigInteger q = BigInteger.probablePrime(1024, random);

        BigInteger n= p.multiply(q);

        BigInteger phi = (p.subtract(BigInteger.ONE) ).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(65537);

        BigInteger d= e.modInverse(phi);

        System.out.println("message: ");
        String msg= a.nextLine();

        BigInteger msgint= new BigInteger(msg.getBytes());

        BigInteger enc= msgint.modPow(e,n);
        BigInteger dec= enc.modPow(d,n);

        String decm= new String(dec.toByteArray());

        System.out.println(enc);
        System.out.println(decm);

    }

}
