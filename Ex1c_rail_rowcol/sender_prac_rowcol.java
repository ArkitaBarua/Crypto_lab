import java.util.*;
import java.io.*;
import java.net.*;


public class sender_prac_rowcol {
    public static void main(String args[]) throws Exception{
        Scanner a = new Scanner(System.in);
        Socket s = new Socket("localhost",5000);
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);

        System.out.println("key: ");
        String keyStr= a.nextLine();
        System.out.println("pt: ");
        String pt= a.nextLine();
        String[] keychar=keyStr.split(" ");
        int col=keychar.length;
        int[] keys= new int[col];
        int id=0;
        for(String c:keychar){
            keys[id++]=Integer.parseInt(c);
        }
        int row= (int) Math.ceil((double)pt.length()/col); ////TAKE MATH.CEIL
        char[][] mat= new char[row][col];
        int idx=0;
        for(int i=0;i<row;i++){ //write rowwise
            for(int j=0;j<col;j++){
                if(idx<pt.length()){
                    mat[i][j]=pt.charAt(idx++);
                }
                else mat[i][j]='*';
            }
        }
        //read colwise
        StringBuilder ct= new StringBuilder();
        for(int k:keys){
            for(int i=0;i<row;i++){
                ct.append(mat[i][k-1]);
            }
        }
        out.println(keyStr);
        out.println(ct);
    }
}
