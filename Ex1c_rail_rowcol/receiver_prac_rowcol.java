import java.util.*;
import java.io.*;
import java.net.*;


public class receiver_prac_rowcol {
    public static void main(String args[]) throws Exception{
        Scanner a = new Scanner(System.in);
        ServerSocket ss = new ServerSocket(5000);
        Socket s = ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String keyStr= in.readLine();
        String ct= in.readLine();
        System.out.println(ct);

        String[] keychar=keyStr.split(" ");
        int col=keychar.length;
        int[] keys= new int[col];
        int id=0;
        for(String c:keychar){
            keys[id++]=Integer.parseInt(c);
        }

        int row= ct.length()/col;
        char[][] mat= new char[row][col];
        int idx=0;
        for(int i:keys){ //write according to key
            for(int j=0;j<row;j++){
                mat[j][i-1]=ct.charAt(idx++);
            }
        }


        //read rowwise
        StringBuilder pt= new StringBuilder();
        for(int j=0;j<row;j++){
            for(int i=0;i<col;i++){
                if(mat[j][i]!='*')
                pt.append(mat[j][i]);
            }
        }
        System.out.println(pt);
    }
}
