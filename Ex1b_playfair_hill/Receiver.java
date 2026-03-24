// Server.java - receiver
import java.io.*;
import java.net.*;

public class Receiver {
    private char[][] matrix;

    public Receiver(String key) {
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

    public String decrypt(String cipher) {
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < cipher.length(); i += 2) {
            char a = cipher.charAt(i);
            char b = cipher.charAt(i + 1);
            int[] posA = find(a);
            int[] posB = find(b);

            if (posA[0] == posB[0]) {
                plain.append(matrix[posA[0]][(posA[1] - 1 + 5) % 5])
                     .append(matrix[posB[0]][(posB[1] - 1 + 5) % 5]);
            } else if (posA[1] == posB[1]) {
                plain.append(matrix[(posA[0] - 1 + 5) % 5][posA[1]])
                     .append(matrix[(posB[0] - 1 + 5) % 5][posB[1]]);
            } else {
                plain.append(matrix[posA[0]][posB[1]])
                     .append(matrix[posB[0]][posA[1]]);
            }
        }

        // Remove filler 'X' between identical letters or at end
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < plain.length(); i++) {
            char c = plain.charAt(i);
            if (c == 'X') {
                if (i > 0 && i < plain.length() - 1 && plain.charAt(i - 1) == plain.charAt(i + 1))
                    continue;
                if (i == plain.length() - 1)
                    continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        final int PORT = 5000;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server ready on port " + PORT);

            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                String key = (String) in.readObject();
                String ciphertext = (String) in.readObject();

                System.out.println("Received key: " + key);
                System.out.println("Received ciphertext: " + ciphertext);

                Receiver r = new Receiver(key);
                String decrypted = r.decrypt(ciphertext);

                System.out.println("Decrypted plaintext: " + decrypted);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}