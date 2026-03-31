// File Name GreetingClient.java
import java.net.*;
import java.io.*;
import java.util.*;

public class rail_client_sender {
        public static void main(String[] args) throws Exception{
   
            Socket socket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner s = new Scanner(System.in);
  
            System.out.println("Connected to the server.");
            System.out.println("Enter depth:");
            int depth=s.nextInt();
            s.nextLine();
            System.out.println("Enter plain text:");
   
            // Send message from user input
            String pt = s.nextLine();

            char[][] railMatrix = new char[depth][pt.length()];

            for (int i = 0; i < depth; i++)
            Arrays.fill(railMatrix[i], '\n');

            boolean dirDown = false;
            int row = 0, col = 0;

            for (int i = 0; i < pt.length(); i++) {
                // Check the direction of flow
                if (row == 0 || row == depth - 1) dirDown = !dirDown;

                  // Fill the corresponding alphabet
                railMatrix[row][col++] = pt.charAt(i);

                  // Find the next row using direction flag
                if (dirDown)
                    row++;
                else
                    row--;
            }
           // Now we can create the cipher
            StringBuilder CT = new StringBuilder();
            for (int i = 0; i < depth; i++)
              for (int j = 0; j < pt.length(); j++)
                if (railMatrix[i][j] != '\n')
                  CT.append(railMatrix[i][j]);
            String ct=CT.toString();



            System.out.println("depth: "+depth+"\nplain text: "+pt + "\ncipher text:"+ct);
            out.println(depth);
            out.println(ct);

            s.close();
            socket.close();


       }
   }