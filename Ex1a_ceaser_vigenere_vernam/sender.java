// client

import java.net.*;
import java.io.*;
import java.util.*;

public class sender {
    public static void main(String[] args) throws Exception {

        Scanner a = new Scanner(System.in);

        System.out.println("Client started...");
        System.out.println("Connecting to server...");
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Connected to server.");
        System.out.print("Enter the string: ");
        String pt = a.nextLine();
        System.out.print("Enter the key: ");
        int k = a.nextInt();
        String ct = "";

        for (int i = 0; i < pt.length(); i++) {
            char c = pt.charAt(i);
            if (Character.isLetter(c)) {
                int shift = k % 26;
                if (Character.isLowerCase(c)) {
                    ct+= (char) ((c - 'a' + shift) % 26 + 'a');
                } else {
                    ct += (char) ((c - 'A' + shift) % 26 + 'A');
                }
            } else {
                ct += c;
            }
        }

        System.out.println("Cipher text: " + ct);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Sending encrypted message and key to server...");
        out.println(ct);
        out.println(k);

        socket.close();
        a.close();
    }
}
