
import java.util.*;
import java.io.*;
import java.net.*;

public class Receiver_prac_rail {
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);
        ServerSocket ss = new ServerSocket(5000);
        Socket s= ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        int depth=Integer.parseInt(in.readLine());
        String ct=in.readLine();

        System.out.println(ct);

        char[][] railmat= new char[depth][ct.length()];

        for(int i=0;i<depth;i++){
            for (int j=0;j<ct.length();j++){
                railmat[i][j]='\n';
            }
        }

        boolean dirdown=true;
        int dir=0;
        for(int i=0;i<ct.length();i++){
            railmat[dir][i]='*';
            if(dirdown){
                dir++;
            }
            else dir--;
            if(dir==0 || dir==depth-1){
                dirdown=!dirdown;
            }
        }
        int idx=0;
        StringBuilder pt= new StringBuilder();
        for(int i=0;i<depth;i++){
            for (int j=0;j<ct.length();j++){
                if(railmat[i][j]=='\n') continue;
                else railmat[i][j]=ct.charAt(idx++);
            }
        }
        dirdown=true;
        dir=0;
        for(int i=0;i<ct.length();i++){
            pt.append(railmat[dir][i]);
            if(dirdown){
                dir++;
            }
            else dir--;
            if(dir==0 || dir==depth-1){
                dirdown=!dirdown;
            }
        }
        System.out.println(pt);


    }
}

