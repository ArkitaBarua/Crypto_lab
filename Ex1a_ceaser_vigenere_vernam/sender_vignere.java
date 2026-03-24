// client

import java.net.*;
import java.io.*;
import java.util.*;

public class sender_vignere {
    public static void main(String[] args) throws Exception {

        Scanner a = new Scanner(System.in);

        System.out.println("client started...");
        System.out.println("connecting to server...");

        Socket s = new Socket("localhost", 5000);
        System.out.println("Connected to server.");

        System.out.print("Enter the string: ");
        String pt = a.nextLine();

        System.out.print("Enter the key: ");
        String k = a.nextLine();

        String ct = "";
        int keyIndex = 0;

        for (int i = 0; i < pt.length(); i++) {
            char m = pt.charAt(i);

            if (Character.isLetter(m)) {
                char c = k.charAt(keyIndex % k.length());
                int shift = Character.toLowerCase(c) - 'a';

                if (Character.isLowerCase(m)) {
                    ct += (char) ((m - 'a' + shift) % 26 + 'a');
                } else {
                    ct += (char) ((m - 'A' + shift) % 26 + 'A');
                }
                keyIndex++;
            } else {
                ct += m;
            }
        }

        System.out.println("cipher text: " + ct);

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        System.out.println("Sending cipher text and key to server...");
        out.println(ct);
        out.println(k);

        System.out.println("Message sent. Closing connection.");

        s.close();
        a.close();
    }
}
