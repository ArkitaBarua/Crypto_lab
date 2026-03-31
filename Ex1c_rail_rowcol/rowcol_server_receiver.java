// server

import java.net.*;
import java.io.*;
import java.util.*;
public class rowcol_server_receiver {

    public static void main(String[] args) throws Exception {

        System.out.println("Server started...");
        ServerSocket serverSocket = new ServerSocket(5000);

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String keyLine = in.readLine();
        String ct = in.readLine();

        System.out.println("Key received: " + keyLine);
        System.out.println("Cipher text received: " + ct);

        String[] keyStr = keyLine.split(" ");
        int col = keyStr.length;
        int[] key = new int[col];

        for (int i = 0; i < col; i++) {
            key[i] = Integer.parseInt(keyStr[i]);
        } //key array

        int len = ct.length();
        int row = len / col;

        char[][] matrix = new char[row][col];

        int index = 0;

        // Fill column-wise using key order
        for (int k = 1; k <= col; k++) {
            int colIndex = -1;
            for (int j = 0; j < col; j++) {
                if (key[j] == k) {
                    colIndex = j;
                    break;
                }
            }

            for (int i = 0; i < row; i++) {
                matrix[i][colIndex] = ct.charAt(index++);
            }
        }

        // Read row-wise to get plain text
        String pt = "";
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                pt += matrix[i][j];
            }
        }

        System.out.println("Plain Text: " + pt);

        socket.close();
        serverSocket.close();
    }
}
