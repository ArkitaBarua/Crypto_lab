// server

import java.net.*;
import java.io.*;

public class receiver_vernem{
    public static void main(String[] args) throws Exception {

        System.out.println("Server starting...");
        ServerSocket ss = new ServerSocket(5000);
        System.out.println("Server waiting for client connection...");

        Socket s = ss.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        String ct = in.readLine();
        String k = in.readLine();

        System.out.println("Cipher Text received: " + ct);
        System.out.println("Key received: " + k);

        String pt = "";

        for (int i = 0; i < ct.length(); i++) {
            char m = ct.charAt(i);
            char c = k.charAt(i);

            if (m >= 'a' && m <= 'z') {
                pt += (char) (((m - 'a') ^ (c - 'a')) + 'a');
            } else {
                pt += m;
            }
        }

        System.out.println("Plain Text: " + pt);
        System.out.println("Closing connection.");

        s.close();
        ss.close();
    }
}
