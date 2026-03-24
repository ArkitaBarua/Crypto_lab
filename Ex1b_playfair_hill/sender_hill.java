import java.net.*;
import java.io.*;
import java.util.*;

public class sender_hill {
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter plaintext (UPPERCASE): ");
        String pt = sc.nextLine().toUpperCase();

        System.out.print("Enter matrix size n: ");
        int n = sc.nextInt();

        if (pt.length() % n != 0) {
            System.out.println("Plaintext length not multiple of matrix size");
            return;
        }

        int[][] key = new int[n][n];
        System.out.println("Enter key matrix:");
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                key[i][j] = sc.nextInt();

        Socket s = new Socket("localhost", 8000);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        String ct = "";

        for (int i = 0; i < pt.length(); i += n) {
            for (int r = 0; r < n; r++) {
                int sum = 0;
                for (int c = 0; c < n; c++)
                    sum += key[r][c] * (pt.charAt(i + c) - 'A');
                ct += (char) ((sum % 26) + 'A');
            }
        }

        System.out.println("Cipher Text (CT): " + ct);

        out.println(n);
        out.println(ct);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                out.print(key[i][j] + " ");
            out.println();
        }

        s.close();
        sc.close();
    }
}
