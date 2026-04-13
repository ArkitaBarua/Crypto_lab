import java.net.*;
import java.io.*;
import java.util.*;

public class receiver_hill {
    public static void main(String[] args) throws Exception {

        System.out.println("Server starting...");
        ServerSocket ss = new ServerSocket(8000);
        Socket s = ss.accept();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        int n = Integer.parseInt(in.readLine());
        String ct = in.readLine();

        System.out.println("Cipher Text received: " + ct);

        int[][] key = new int[n][n];

        // using split()
        for (int i = 0; i < n; i++) {
            String[] parts = in.readLine().trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                key[i][j] = Integer.parseInt(parts[j]);
            }
        }

        // NEW: inverse using your function
        int[][] invKey = invertMatrixMod(key, 26); //mod'26'

        StringBuilder pt = new StringBuilder();

        // 🔥 C × K⁻¹ (row vector version)
        for (int i = 0; i < ct.length(); i += n) {

            for (int c = 0; c < n; c++) {
                int sum = 0;

                for (int r = 0; r < n; r++) {
                    sum += (ct.charAt(i + r) - 'A') * invKey[r][c];
                }

                pt.append((char) (((sum % 26) + 26) % 26 + 'A'));
            }
        }

        System.out.println("Plain Text (PT): " + pt);

        s.close();
        ss.close();
    }

    // 🔥 YOUR FUNCTION (unchanged)
    private static int[][] invertMatrixMod(int[][] matrix, int m) {
        int n = matrix.length;
        int det = determinant(matrix) % m;
        if (det < 0) det += m; ////

        int invDet = modInverse(det, m);
        int[][] adjugate = adjugate(matrix);
        int[][] inverse = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = (adjugate[i][j] * invDet) % m;
                if (inverse[i][j] < 0) inverse[i][j] += m;
            }
        }
        return inverse;
    }

    // 🔹 Determinant (recursive)
    private static int determinant(int[][] matrix) {
        int n = matrix.length;

        if (n == 1) return matrix[0][0];

        int det = 0;
        for (int c = 0; c < n; c++) {
            det += Math.pow(-1, c) * matrix[0][c] * determinant(getMinor(matrix, 0, c));
        }
        return det;
    }

    // 🔹 Get minor matrix
    private static int[][] getMinor(int[][] matrix, int row, int col) {
        int n = matrix.length;
        int[][] minor = new int[n - 1][n - 1];

        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue; //skip

            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue; //skip
                minor[r][c++] = matrix[i][j];
            }
            r++;
        }
        return minor;
    }

    // 🔹 Adjugate
    private static int[][] adjugate(int[][] matrix) {
        int n = matrix.length;
        int[][] adj = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                adj[j][i] = sign * determinant(getMinor(matrix, i, j)); ///// transpose here
            }
        }
        return adj;
    }

    // 🔹 Modular inverse (brute force)
    private static int modInverse(int a, int m) { //a-1modm
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1)
                return x;
        }
        throw new ArithmeticException("Inverse does not exist");
    }
}