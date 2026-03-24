// server

import java.net.*;
import java.io.*;

public class receiver_vignere {
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

        System.out.println("cipher text received: " + ct);
        System.out.println("Key received: " + k);

        String pt = "";
        int keyIndex = 0;

        for (int i = 0; i < ct.length(); i++) {
            char c = ct.charAt(i);

            if (Character.isLetter(c)) {
                char ch = k.charAt(keyIndex % k.length());
                int shift = Character.toLowerCase(ch) - 'a';

                if (Character.isLowerCase(c)) {
                    pt += (char) ((c - 'a' - shift + 26) % 26 + 'a');
                } else {
                    pt += (char) ((c - 'A' - shift + 26) % 26 + 'A');
                }
                keyIndex++;
            } else {
                pt+= c;
            }
        }

        System.out.println("plain text: " + pt);
        System.out.println("closing connection.");

        s.close();
        ss.close();
    }
}
