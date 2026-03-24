import java.util.Scanner;

public class md5 {

    /* ---------- Shift amounts ---------- */
    private static final int[] SHIFT = {
        7,12,17,22, 7,12,17,22, 7,12,17,22, 7,12,17,22,
        5,9,14,20, 5,9,14,20, 5,9,14,20, 5,9,14,20,
        4,11,16,23, 4,11,16,23, 4,11,16,23, 4,11,16,23,
        6,10,15,21, 6,10,15,21, 6,10,15,21, 6,10,15,21
    };

    /* ---------- Constants ---------- */
    private static final int[] T = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            T[i] = (int)((1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Input: ");
        String input = sc.nextLine();

        System.out.println("Number of characters in input: " + input.length());

        byte[] msg = input.getBytes();
        int originalLength = msg.length;
        long bitLength = (long) originalLength * 8;

        /* ---------- Padding ---------- */
        int padLen = (56 - (originalLength + 1) % 64 + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padLen + 8];

        System.arraycopy(msg, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;

        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte)(bitLength >>> (8 * i));
        }

        /* ---------- Initial Values ---------- */
        int A = 0x67452301;
        int B = 0xefcdab89;
        int C = 0x98badcfe;
        int D = 0x10325476;

        int totalBlocks = padded.length / 64;

        for (int block = 0; block < totalBlocks; block++) {

            System.out.println("\nBlock " + (block + 1));

            int[] X = new int[16];

            for (int j = 0; j < 16; j++) {
                int idx = block * 64 + j * 4;
                X[j] = ((padded[idx] & 0xff)) |
                       ((padded[idx + 1] & 0xff) << 8) |
                       ((padded[idx + 2] & 0xff) << 16) |
                       ((padded[idx + 3] & 0xff) << 24);
            }

            int a = A, b = B, c = C, d = D;

            for (int i = 0; i < 64; i++) {

                int F, g;

                if (i < 16) {
                    F = (b & c) | (~b & d);
                    g = i;
                }
                else if (i < 32) {
                    F = (d & b) | (~d & c);
                    g = (5 * i + 1) % 16;
                }
                else if (i < 48) {
                    F = b ^ c ^ d;
                    g = (3 * i + 5) % 16;
                }
                else {
                    F = c ^ (b | ~d);
                    g = (7 * i) % 16;
                }

                int temp = d;
                d = c;
                c = b;

                int sum = a + F + T[i] + X[g];
                b = b + Integer.rotateLeft(sum, SHIFT[i]);
                a = temp;

                /* ---- Print at end of each round ---- */
                if (i == 15) {
                    System.out.printf("Round 1:\nA=%08x B=%08x C=%08x D=%08x\n", a,b,c,d);
                }
                if (i == 31) {
                    System.out.printf("Round 2:\nA=%08x B=%08x C=%08x D=%08x\n", a,b,c,d);
                }
                if (i == 47) {
                    System.out.printf("Round 3:\nA=%08x B=%08x C=%08x D=%08x\n", a,b,c,d);
                }
                if (i == 63) {
                    System.out.printf("Round 4:\nA=%08x B=%08x C=%08x D=%08x\n", a,b,c,d);
                }
            }

            A += a;
            B += b;
            C += c;
            D += d;
        }

        String finalHash = String.format("%08x%08x%08x%08x", A, B, C, D);
        System.out.println("\nFinal Hash Value: " + finalHash);
    }
}