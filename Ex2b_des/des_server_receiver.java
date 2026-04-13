
import java.io.*;
import java.net.*;
 
public class des_server_receiver {

    // ---------- DES TABLES ----------
    static final int[] IP = {
        58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7
    };

    static final int[] FP = {
        40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,33,1,41,9,49,17,57,25
    };

    static final int[] E = {
        32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,
        16,17,18,19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1
    };

    static final int[] P = {
        16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,19,13,30,6,22,11,4,25
    };

    static final int[] PC1 = {
        57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,
        60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,
        29,21,13,5,28,20,12,4
    };

    static final int[] PC2 = {
        14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,
        26,8,16,7,27,20,13,2,41,52,31,37,47,55,30,40,
        51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32
    };

    static final int[][][] S = des_client_sender.S; 

    // Permutation
    static long permute(long v, int[] table, int inBits, int outBits) {
        long r = 0;
        for (int i = 0; i < outBits; i++) {
            int bit = (int)((v >> (inBits - table[i])) & 1L);
            r = (r << 1) | bit;
        }
        return r;
    }

    // Rotate
    static long rotl28(long x, int n) {
        return ((x << n) | (x >> (28 - n))) & 0x0FFFFFFFL;
    }

    // Generate subkeys
    static long[] generateSubKeys(long key64) {
        long k56 = permute(key64, PC1, 64, 56);
        long c = (k56 >> 28) & 0x0FFFFFFFL;
        long d = k56 & 0x0FFFFFFFL;

        int[] shifts = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
        long[] keys = new long[16];

        for (int i = 0; i < 16; i++) {
            c = rotl28(c, shifts[i]);
            d = rotl28(d, shifts[i]);
            long cd = (c << 28) | d;
            keys[i] = permute(cd, PC2, 56, 48);
        }
        return keys;
    }

    // Feistel
    static long feistel(long r, long k) {
        long e48 = permute(r, E, 32, 48);
        long x = e48 ^ k;
        long out = 0;

        for (int i = 0; i < 8; i++) {
            int six = (int)((x >> (42 - 6*i)) & 0x3F);
            int row = ((six >> 5) << 1) | (six & 1);
            int col = (six >> 1) & 0xF;
            int val = S[i][row][col];
            out = (out << 4) | val;
        }
        return permute(out, P, 32, 32);
    }

    // Decryption (reverse key order)
    static long decryptBlock(long block, long[] keys, StringBuilder dbg) {

        long ip = permute(block, IP, 64, 64);

        long l = (ip >> 32) & 0xFFFFFFFFL;
        long r = ip & 0xFFFFFFFFL;

        for (int i = 0; i < 16; i++) {
            long f = feistel(r, keys[15 - i]); // reverse
            long nl = r;
            long nr = l ^ f;
            l = nl; r = nr;
        }

        long pre = (r << 32) | l;
        return permute(pre, FP, 64, 64);
    }

    static long hexToLong(String h) {
        return Long.parseUnsignedLong(h,16);
    }

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(5000);
        Socket s = ss.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String ctHex = in.readLine();
        String keyHex = in.readLine();

        long key64 = hexToLong(keyHex);
        long[] keys = generateSubKeys(key64);

        StringBuilder pt = new StringBuilder();

        for (int i = 0; i < ctHex.length(); i += 16) {
            String blk = ctHex.substring(i, i+16);
            long block = hexToLong(blk);

            long dec = decryptBlock(block, keys, new StringBuilder());

            for (int j = 0; j < 8; j++) {
                pt.append((char)((dec >> (56 - 8*j)) & 0xFF));
            }
        }

        System.out.println("Decrypted Text: " + pt);

        s.close();
        ss.close();
    }
}