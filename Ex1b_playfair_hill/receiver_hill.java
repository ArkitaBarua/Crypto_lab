import java.net.*;
import java.io.*;
import java.util.*;

public class receiver_hill {
    public static void main(String[] args) throws Exception {

        System.out.println("Server starting...");
        ServerSocket ss = new ServerSocket(8000);
        System.out.println("Server waiting for client connection...");

        Socket s = ss.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        int n = Integer.parseInt(in.readLine());

        String ct = in.readLine();
        System.out.println("Cipher Text received: " + ct);

        int[][] key = new int[n][n];
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(in.readLine());
            for (int j = 0; j < n; j++) {
                key[i][j] = Integer.parseInt(st.nextToken());
            }
        }


        // Augmented matrix [K | I]
        int[][] aug = new int[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                aug[i][j] = key[i][j] % 26;
            aug[i][i + n] = 1;
        }

        // Gauss–Jordan elimination (mod 26)
        for (int i = 0; i < n; i++) {
            int inv = 0;
            for (int x = 1; x < 26; x++)
                if ((aug[i][i] * x) % 26 == 1)
                    inv = x;

            for (int j = 0; j < 2 * n; j++)
                aug[i][j] = (aug[i][j] * inv) % 26;

            for (int k = 0; k < n; k++) {
                if (k != i) {
                    int factor = aug[k][i];
                    for (int j = 0; j < 2 * n; j++)
                        aug[k][j] = (aug[k][j] - factor * aug[i][j]) % 26;
                }
            }
        }

        int[][] invKey = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                invKey[i][j] = (aug[i][j + n] + 26) % 26;

        String pt = "";

        for (int i = 0; i < ct.length(); i += n) {
            for (int r = 0; r < n; r++) {
                int sum = 0;
                for (int c = 0; c < n; c++)
                    sum += invKey[r][c] * (ct.charAt(i + c) - 'A');
                pt += (char) ((sum % 26) + 'A');
            }
        }

        System.out.println("Plain Text (PT): " + pt);
        System.out.println("Closing connection.");

        s.close();
        ss.close();
    }
}
