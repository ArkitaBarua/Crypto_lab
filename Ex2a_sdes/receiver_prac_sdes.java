
import java.util.*;
import java.io.*;
import java.net.*;

public class receiver_prac_sdes {
    static int[] P10 = {3,5,2,7,4,10,1,9,8,6};
    static int[] P8  = {6,3,7,4,8,5,10,9};
    static int[] IP  = {2,6,3,1,4,8,5,7};
    static int[] IP_INV = {4,1,3,5,7,2,8,6};
    static int[] EP = {4,1,2,3,2,3,4,1};
    static int[] P4 = {2,4,3,1};

    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);

        ServerSocket ss = new ServerSocket(5000);
        Socket s= ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String key = in.readLine();
        String pt= in.readLine();

        //subkey generation
        String p10 = permute(key,P10);
        String left= p10.substring(0,5);
        String right=p10.substring(5,10);
        left=shift(left,1);
        right=shift(right,1);

        String k1= permute(left+right,P8);

        System.out.println("subkey 1: "+k1);

        left=shift(left,2);
        right=shift(right,2);

        String k2= permute(left+right,P8);
        System.out.println("subkey 2: "+k2);

        String ip= permute(pt, IP);
        String r1= fk(ip,k2);
        System.out.println("round1: "+r1);
        r1=r1.substring(4)+r1.substring(0,4);

        String r2= fk(r1,k1);
        System.out.println("round2: "+r2);

        String ipinv= permute(r2,IP_INV);

        System.out.println(ipinv);


    }

    public static String permute(String s, int[] perm){
        StringBuilder res = new StringBuilder();
        for(int i : perm){
            res.append(s.charAt(i-1));
        }
        return res.toString();
    }

    public static String shift(String s, int n){
        return s.substring(n)+s.substring(0,n);
    }
    public static String fk(String s, String k){
        String left= s.substring(0,4);
        String right=s.substring(4);
        right= permute(right,EP);
        right= xor(right,k);
        String s1=sbox(right.substring(0,4),S0);
        String s2=sbox(right.substring(4),S1);
        right=permute(s1+s2,P4);
        
        return xor(right,left)+s.substring(4);
    }

    public static String xor(String a, String b){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<a.length();i++){
            if (a.charAt(i)==b.charAt(i)){
                s.append('0');
            }
            else s.append('1');
        }
        return s.toString();
    }
    public static String sbox(String s, int[][] box){
        int row= Integer.parseInt(""+s.charAt(0)+s.charAt(3),2);
        int col= Integer.parseInt(""+s.charAt(1)+s.charAt(2),2);
        int val= box[row][col];
        return String.format("%2s", Integer.toBinaryString(val)).replace(' ','0');
    }
}
