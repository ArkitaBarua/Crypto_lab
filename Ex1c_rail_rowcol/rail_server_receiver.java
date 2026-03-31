import java.net.*;
import java.io.*;
import java.util.*;

public class rail_server_receiver {

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(5000);

        // Wait for client
        Socket s = ss.accept();
        System.out.println("New client connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        // Read message sent by client
        int depth = Integer.parseInt(in.readLine());
        String ct = in.readLine();

        System.out.println("Depth: " + depth);
        System.out.println("Cipher text: " + ct);

        char[][] railMatrix = new char[depth][ct.length()];

        // Initialize matrix
        for (int i = 0; i < depth; i++) {
            Arrays.fill(railMatrix[i], '\n');
        }

        boolean dirDown = true;
        int row = 0, col = 0;

        // Mark the positions with '*'
        for (int i = 0; i < ct.length(); i++) {
            if (row == 0)
                dirDown = true;
            if (row == depth - 1)
                dirDown = false;

            railMatrix[row][col++] = '*';

            if (dirDown)
                row++;
            else
               row--;
        }

            // Fill the matrix with cipher text, row wise
        int index = 0;
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < ct.length(); j++) {
                if (railMatrix[i][j] == '*' && index < ct.length()) {
                    railMatrix[i][j] = ct.charAt(index++);
                }
            }
        }

        // Read the matrix in zig-zag to get plain text
        StringBuilder result = new StringBuilder();

        row = 0;
        col = 0;
        for (int i = 0; i < ct.length(); i++) {

            if (row == 0)
                dirDown = true;
            if (row == depth - 1)
                dirDown = false;

            if (railMatrix[row][col] != '*')
                result.append(railMatrix[row][col++]);

            if (dirDown)
                row++;
            else
                row--;
        }

        String pt = result.toString();
        System.out.println("Plain text: " + pt);
    

    s.close();
    ss.close();
}
}
