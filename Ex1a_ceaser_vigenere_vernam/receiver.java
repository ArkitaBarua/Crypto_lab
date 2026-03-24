// server

import java.net.*;
import java.io.*;

public class receiver {
    public static void main(String[] args) throws Exception {

        System.out.println("Server starting...");
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server waiting for client connection...");

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String ct = in.readLine();
        int k = Integer.parseInt(in.readLine());

        System.out.println("Cipher text received: " + ct);
        System.out.println("Key received: " + k);

        String pt = "";

        for (int i = 0; i < ct.length(); i++) {
            char c = ct.charAt(i);

            if (Character.isLetter(c)) {
                int shift = k % 26;

                if (Character.isLowerCase(c)) {
                    pt += (char) ((c - 'a' - shift + 26) % 26 + 'a');
                } else {
                    pt += (char) ((c - 'A' - shift + 26) % 26 + 'A');
                }
            } else {
                pt += c;
            }
        }

        System.out.println("plain text: " + pt);

        socket.close();
        serverSocket.close();
    }
}
