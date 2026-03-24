
import java.net.*;
import java.io.*;

public class sdes_server_receiver {

    // Same permutation tables
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

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Waiting for sender(client)");

        Socket socket = serverSocket.accept();
        System.out.println("client connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String ct = in.readLine();
        String k1 = in.readLine();
        String k2 = in.readLine();

        System.out.println("\nReceived Cipher Text: " + ct);
        System.out.println("Subkeys:");
        System.out.println("K1: " + k1);
        System.out.println("K2: " + k2);

        String ip = permute(ct, IP);
        System.out.println("IP: " + ip);

        String r1 = fk(ip, k2);
        System.out.println("Round1: " + r1);

        r1 = r1.substring(4) + r1.substring(0,4);

        String r2 = fk(r1, k1);
        System.out.println("Round2: " + r2);

        String pt = permute(r2, IP_INV);
        System.out.println("Plaintext: " + pt);

        socket.close();
        serverSocket.close();
    }

    // ---------- Utility Functions ----------

    static String permute(String input, int[] table) {
        String output = "";
        for(int i : table)
            output += input.charAt(i-1);
        return output;
    }

    static String xor(String a, String b) {
        String result = "";
        for(int i=0;i<a.length();i++)
            result += (a.charAt(i)==b.charAt(i)) ? '0' : '1';
        return result;
    }

    static String fk(String input, String key) {
        String left = input.substring(0,4);
        String right = input.substring(4);

        String ep = permute(right, EP);
        String xor = xor(ep, key);

        String s0 = sBox(xor.substring(0,4), S0);
        String s1 = sBox(xor.substring(4), S1);

        String p4 = permute(s0+s1, P4);
        String result = xor(left, p4) + right;

        return result;
    }

    static String sBox(String input, int[][] box) {
        int row = Integer.parseInt("" + input.charAt(0) + input.charAt(3),2);
        int col = Integer.parseInt("" + input.charAt(1) + input.charAt(2),2);
        int val = box[row][col];
        return String.format("%2s", Integer.toBinaryString(val)).replace(' ','0');
    }
}
