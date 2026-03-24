// Client.java - sender
//playfair
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Sender {
    private char[][] matrix;

    public Sender(String key) {
        buildMatrix(key.toUpperCase().replace('J', 'I'));
    }

    private void buildMatrix(String key) {
        boolean[] used = new boolean[26];
        matrix = new char[5][5];
        int idx = 0;

        for (char c : key.toCharArray()) {
            if (c >= 'A' && c <= 'Z' && !used[c - 'A']) {
                used[c - 'A'] = true;
                matrix[idx / 5][idx % 5] = c;
                idx++;
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'J') continue;
            if (!used[c - 'A']) {
                matrix[idx / 5][idx % 5] = c;
                idx++;
            }
        }
    }

    private String preprocess(String text) {
        StringBuilder clean = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                clean.append(c == 'J' ? 'I' : c);
            }
        }
    
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < clean.length()) {
            char a = clean.charAt(i);
            if (i == clean.length() - 1) {
                // Last character, odd length
                result.append(a).append('X');
                break;
            }
            char b = clean.charAt(i + 1);
            if (a == b) {
                // Same letter: use 'X' as filler, and do NOT consume b yet
                result.append(a).append('X');
                i++; // move only by 1, so b is reused as first letter next time
            } else {
                result.append(a).append(b);
                i += 2;
            }
        }
        return result.toString();
    }
    private String insertFillers(String s) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            res.append(c);
            if (i + 1 < s.length() && s.charAt(i + 1) == c) {
                res.append('X');
            }
        }
        if (res.length() % 2 == 1) {
            res.append('X');
        }
        return res.toString();
    }

    private int[] find(char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public String encrypt(String plain) {
        String clean = preprocess(plain);
        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < clean.length(); i += 2) {
            char a = clean.charAt(i);
            char b = clean.charAt(i + 1);
            int[] posA = find(a);
            int[] posB = find(b);

            if (posA[0] == posB[0]) {
                cipher.append(matrix[posA[0]][(posA[1] + 1) % 5])
                      .append(matrix[posB[0]][(posB[1] + 1) % 5]);
            } else if (posA[1] == posB[1]) {
                cipher.append(matrix[(posA[0] + 1) % 5][posA[1]])
                      .append(matrix[(posB[0] + 1) % 5][posB[1]]);
            } else {
                cipher.append(matrix[posA[0]][posB[1]])
                      .append(matrix[posB[0]][posA[1]]);
            }
        }
        return cipher.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter key: ");
        String key = sc.nextLine().trim();

        System.out.print("Enter plaintext: ");
        String plaintext = sc.nextLine().trim();

        Sender client = new Sender(key);
        String ciphertext = client.encrypt(plaintext);

        System.out.println("Ciphertext: " + ciphertext);

        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject(key);
            out.writeObject(ciphertext);
            out.flush();

            System.out.println("Sent to server.");

        } catch (IOException e) {
            e.printStackTrace();
        }
        sc.close();
    }
}