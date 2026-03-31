//client

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Sender {
    private char[][] matrix;

    // Constructor: builds 5x5 Playfair matrix from key
    public Sender(String key) {
        buildMatrix(key.toUpperCase().replace('J', 'I'));
    }

    // Step 1: Build Playfair matrix (5x5)
    private void buildMatrix(String key) {
        boolean[] used = new boolean[26];
        matrix = new char[5][5];
        int idx = 0;

        // Insert key letters first (no duplicates)
        for (char c : key.toCharArray()) {
            if (c >= 'A' && c <= 'Z' && !used[c - 'A']) {
                used[c - 'A'] = true;
                matrix[idx / 5][idx % 5] = c;
                idx++;
            }
        }

        // Fill remaining letters (excluding J)
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'J') continue;
            if (!used[c - 'A']) {
                matrix[idx / 5][idx % 5] = c;
                idx++;
            }
        }
    }

    // Step 2: Preprocess plaintext
    private String preprocess(String text) {
        StringBuilder clean = new StringBuilder();

        // Remove non-letters + convert J→I
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                clean.append(c == 'J' ? 'I' : c);
            }
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < clean.length()) {
            char a = clean.charAt(i); /////

            // If last character → add filler X
            if (i == clean.length() - 1) {
                result.append(a).append('X');
                break;
            }

            char b = clean.charAt(i + 1); //////

            // If both letters same → insert X between them
            if (a == b) {
                result.append(a).append('X');
                i++; // move only one step //////
            } else {
                result.append(a).append(b);
                i += 2;
            }
        }
        return result.toString();
    }

    // Find position of character in matrix
    private int[] find(char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; ///new int[]
    }

    // Step 3: Encrypt using Playfair rules
    public String encrypt(String plain) {
        String clean = preprocess(plain);
        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < clean.length(); i += 2) { //2
            char a = clean.charAt(i);
            char b = clean.charAt(i + 1);

            int[] posA = find(a);
            int[] posB = find(b);

            // Rule 1: Same row → shift right
            if (posA[0] == posB[0]) {
                cipher.append(matrix[posA[0]][(posA[1] + 1) % 5])
                      .append(matrix[posB[0]][(posB[1] + 1) % 5]);
            }
            // Rule 2: Same column → shift down
            else if (posA[1] == posB[1]) {
                cipher.append(matrix[(posA[0] + 1) % 5][posA[1]])
                      .append(matrix[(posB[0] + 1) % 5][posB[1]]);
            }
            // Rule 3: Rectangle → swap columns
            else {
                cipher.append(matrix[posA[0]][posB[1]])
                      .append(matrix[posB[0]][posA[1]]);
            }
        }
        return cipher.toString();
    }

    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter key: ");
        String key = sc.nextLine().trim();

        System.out.print("Enter plaintext: ");
        String plaintext = sc.nextLine().trim();

        Sender client = new Sender(key);
        String ciphertext = client.encrypt(plaintext);

        System.out.println("Ciphertext: " + ciphertext);

        Socket socket = new Socket("localhost", 5000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send data line-by-line
        out.println(key);
        out.println(ciphertext);

        System.out.println("Sent to server.");

        out.close();
        socket.close();
    }
}