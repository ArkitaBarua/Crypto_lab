import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver_prac_play {
    char[][] matrix;
    public Receiver_prac_play(String key){
        buildmatrix(key.toUpperCase().replace('J','I'));
    }
    public void buildmatrix(String key){
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
    public int[] find(char c){
        for(int i=0;i<5;i++){
            for (int j=0;j<5;j++){
                if(matrix[i][j]==c){
                    return new int[]{i,j};
                }
            }
        }

        return new int[]{-1,-1};
    }
    public String decrypt(String ct){
        StringBuilder pt= new StringBuilder();
        int i=0;
        while(i<ct.length()){
            char a=ct.charAt(i);
            char b=ct.charAt(i+1);

            int[] posa= find(a);
            int[] posb= find(b);

            //same row
            if(posa[0]==posb[0]){
                pt.append(matrix[posa[0]][(posa[1]-1+5)%5]);
                pt.append(matrix[posb[0]][(posb[1]-1+5)%5]);
            }
            //same col
            else if(posa[1]==posb[1]){
                pt.append(matrix[(posa[0]-1+5)%5][posa[1]]);
                pt.append(matrix[(posb[0]-1+5)%5][posb[1]]);
            }
            //rectangle
            else{
                pt.append(matrix[posa[0]][posb[1]]);
                pt.append(matrix[posb[0]][posa[1]]);
            }
            i+=2;
        }

        return pt.toString();
    }
    public static void main(String args[]) throws IOException{
        ServerSocket ss = new ServerSocket(5000);
        Socket s= ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String key= in.readLine();
        String ct = in.readLine();
        Receiver_prac_play rec= new Receiver_prac_play(key);
        System.out.println(ct);
        String pt = rec.decrypt(ct);
        System.out.println(pt);
    }
}
