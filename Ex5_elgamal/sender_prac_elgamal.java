import java.util.*;
import java.math.BigInteger;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class sender_prac_elgamal {
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);
        Socket s = new Socket("localhost",5000);
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        BigInteger q= new BigInteger(in.readLine());
        BigInteger alpha= new BigInteger(in.readLine());
        BigInteger y= new BigInteger(in.readLine());

        System.out.println("message: ");
        BigInteger m= new BigInteger(a.nextLine());

        SecureRandom random = new SecureRandom();
        BigInteger k;
        do{
            k= new BigInteger(q.bitLength(),random);
        } while( k.compareTo(BigInteger.ZERO)<=0 || k.compareTo(q)<=0);

        //encryption

        BigInteger K= y.modPow(k,q);
        BigInteger C1= alpha.modPow(k,q);
        BigInteger C2= (K.multiply(m)).mod(q);

        out.println(C1.toString());
        out.println(C2.toString());
    }
}
