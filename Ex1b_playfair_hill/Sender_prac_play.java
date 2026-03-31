import java.io.*;
import java.net.*;
import java.util.*;

public class Sender_prac_play {
    static char[][] matrix;
    public static void buildmatrix(String key){
        matrix= new char[5][5];
        int idx=0;
        boolean[] used=new boolean[26];

        for(char c:key.toCharArray()){
            if(c<='Z' && c>='A' && !used[c-'A']){
                matrix[idx/5][idx%5]=c;
                idx++;
                used[c-'A']=true;
            }
        }
        for(char c='A';c<='Z';c++){
            if (c=='J') continue;
            if(!used[c-'A']){
                matrix[idx/5][idx%5]=c;
                idx++;
                used[c-'A']=true;
            }
        }

    }
    public static String preprocess(String pt){
        StringBuilder clean= new StringBuilder();
        pt=pt.toUpperCase().replace('J','I');
        for(char c:pt.toCharArray()){
            if(c<='Z' && c>='A'){
                clean.append(c);
            }
        }
        StringBuilder res= new StringBuilder();
        int i=0;
        while(i<clean.length()){
            char a=clean.charAt(i);
            if(i==clean.length()-1){
                res.append(a).append('X');
                break;
            }
            char b=clean.charAt(i+1);
            if(a==b){
                res.append(a).append('X');
                i++;
            }
            else{
                res.append(a).append(b);
                i+=2;
            }

        }
        return res.toString();
    }
    public static int[] find(char c){
        for(int i=0;i<5;i++){
            for (int j=0;j<5;j++){
                if(matrix[i][j]==c){
                    return new int[]{i,j};
                }
            }
        }

        return new int[]{-1,-1};
    }
    public static String encrypt(String plain){
        String clean=preprocess(plain);
        StringBuilder ct= new StringBuilder();
        int i=0;
        while(i<clean.length()){
            char a=clean.charAt(i);
            char b=clean.charAt(i+1);

            int[] posa= find(a);
            int[] posb= find(b);

            //same row
            if(posa[0]==posb[0]){
                ct.append(matrix[posa[0]][(posa[1]+1)%5]);
                ct.append(matrix[posb[0]][(posb[1]+1)%5]);
            }
            //same col
            else if(posa[1]==posb[1]){
                ct.append(matrix[(posa[0]+1)%5][posa[1]]);
                ct.append(matrix[(posb[0]+1)%5][posb[1]]);
            }
            //rectangle
            else{
                ct.append(matrix[posa[0]][posb[1]]);
                ct.append(matrix[posb[0]][posa[1]]);
            }
            i+=2;
        }

        return ct.toString();
    }
    public static void main(String args[]) throws IOException{
        Scanner a = new Scanner(System.in);
        System.out.println("Write key: ");
        String key=a.nextLine();
        System.out.println("Write STring: ");
        String pt=a.nextLine();
        Socket s = new Socket("localhost",5000);
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        out.println(key);
        buildmatrix(key);
        String ct = encrypt(pt);
        System.out.println(ct);
        out.println(ct);
    }
}
