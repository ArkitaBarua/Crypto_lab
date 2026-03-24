// client

import java.net.*;
import java.io.*;
import java.util.*;

public class sender_vernem {
    public static void main(String[] args) throws Exception {

        Scanner a = new Scanner(System.in);

        System.out.print("Enter the plaintext: ");
        String pt = a.nextLine();

        System.out.print("Enter the key (same length): ");
        String k = a.nextLine();

        if (pt.length() != k.length()) {
            System.out.println("Not same length");
            a.close();
            return;
        }

        System.out.println("Client started...");
        System.out.println("Connecting to server...");

        Socket s = new Socket("localhost", 5000);
        System.out.println("Connected to server.");

        String ct = "";

        for (int i = 0; i < pt.length(); i++) {
            char m = pt.charAt(i);
            char c = k.charAt(i);

            if (m >= 'a' && m <= 'z') {
                ct += (char) (((m - 'a') ^ (c - 'a')) + 'a');
            } else {
                ct += m;
            }
        }

        System.out.println("Cipher Text (CT): " + ct);

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        out.println(ct);
        out.println(k);

        System.out.println("Message sent. Closing connection.");

        s.close();
        a.close();
    }
}
