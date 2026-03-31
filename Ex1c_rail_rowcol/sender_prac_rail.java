import java.util.*;
import java.io.*;
import java.net.*;

public class sender_prac_rail {
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);
        Socket s = new Socket("localhost",5000);
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        System.out.println("depth: ");
        int depth=Integer.parseInt(a.nextLine());
        System.out.println("string: ");
        String pt=a.nextLine();

        char[][] railmat= new char[depth][pt.length()];

        for(int i=0;i<depth;i++){
            for (int j=0;j<pt.length();j++){
                railmat[i][j]='\n';
            }
        }

        boolean dirdown=true;
        int dir=0;
        for(int i=0;i<pt.length();i++){
            railmat[dir][i]=pt.charAt(i);
            if(dirdown){
                dir++;
            }
            else dir--;
            if(dir==0 || dir==depth-1){
                dirdown=!dirdown;
            }
        }
        StringBuilder ct= new StringBuilder();
        for(int i=0;i<depth;i++){
            for (int j=0;j<pt.length();j++){
                if(railmat[i][j]=='\n') continue;
                else ct.append(railmat[i][j]);
            }
        }
        out.println(depth);
        out.println(ct);


    }
}
