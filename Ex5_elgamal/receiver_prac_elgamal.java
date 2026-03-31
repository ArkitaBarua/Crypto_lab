import java.util.*;
import java.math.BigInteger;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class receiver_prac_elgamal {
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);
        ServerSocket ss = new ServerSocket(5000);
        Socket s = ss.accept();
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        SecureRandom random= new SecureRandom();
        BigInteger q= BigInteger.probablePrime(16,random);
        BigInteger alpha= primitiveroot(q);
        BigInteger x =  new BigInteger(q.bitLength()-1,random);
        BigInteger y= alpha.modPow(x,q);

        out.println(q);
        out.println(alpha);
        out.println(y);

        //decryption
        BigInteger C1= new BigInteger(in.readLine());
        BigInteger C2= new BigInteger(in.readLine());
        BigInteger K= C1.modPow(x,q);
        BigInteger kinv= K.modInverse(q);
        BigInteger M= (C2.multiply(kinv)).mod(q);

        System.out.println(M.toString());
    }

    public static BigInteger primitiveroot(BigInteger q){
        BigInteger phi= q.subtract(BigInteger.ONE);
        for(BigInteger g= BigInteger.TWO; g.compareTo(q)<0;g.add(BigInteger.ONE)){
            if(g.modPow(phi,q).equals(BigInteger.ONE)){
                return g;
            }
        }
        return BigInteger.TWO;
    }
}

