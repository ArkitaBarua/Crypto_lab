// client

import java.net.*;
import java.io.*;
import java.util.*;

public class rowcol_client_sender {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("Client started...");
        System.out.println("Connecting to server...");
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Connected to server.");

        System.out.print("Enter Plain Text: ");
        String pt = sc.nextLine().replaceAll("\\s+", ""); ////////clean, only letters

        System.out.print("Enter Key (space separated numbers like 4 3 1 2 5 6 7): ");
        String keyLine = sc.nextLine();

        String[] keyStr = keyLine.split(" ");
        int col = keyStr.length;
        int[] key = new int[col];

        for (int i = 0; i < col; i++) {
            key[i] = Integer.parseInt(keyStr[i]);
        } /////key as array

        int len = pt.length();
        int row = (int) Math.ceil((double) len / col); //number of rows? len/keylen

        char[][] matrix = new char[row][col];

        int index = 0;

        // Fill row-wise
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (index < len)
                    matrix[i][j] = pt.charAt(index++);
                else
                    matrix[i][j] = '*';   // padding
            }
        }

        // Generate cipher text using key order
        String ct = "";

        for (int k = 1; k <= col; k++) {
            int colIndex = -1;
            for (int j = 0; j < col; j++) {
                if (key[j] == k) {
                    colIndex = j;
                    break;
                }
            }

            for (int i = 0; i < row; i++) {
                ct += matrix[i][colIndex];
            }
        }

        System.out.println("Cipher Text: " + ct);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send key and cipher text
        out.println(keyLine);
        out.println(ct);

        socket.close();
        sc.close();
    }
}
