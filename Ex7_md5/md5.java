import java.util.Scanner;

public class md5 {

    /* ---------- Shift amounts ---------- */
    // Predefined rotation values used in each of the 64 steps
    private static final int[] SHIFT = {
        7,12,17,22, 7,12,17,22, 7,12,17,22, 7,12,17,22,
        5,9,14,20, 5,9,14,20, 5,9,14,20, 5,9,14,20,
        4,11,16,23, 4,11,16,23, 4,11,16,23, 4,11,16,23,
        6,10,15,21, 6,10,15,21, 6,10,15,21, 6,10,15,21
    };

    /* ---------- Constants ---------- */
    // T[i] = floor(2^32 × abs(sin(i+1)))
    private static final int[] T = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            // Precompute constants using sine values
            T[i] = (int)((1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Input: ");
        String input = sc.nextLine();

        System.out.println("Number of characters in input: " + input.length());

        // Convert input string → byte array
        byte[] msg = input.getBytes();
        int originalLength = msg.length; //(length in bytes)

        // Length of message in bits (needed at end of padding)
        long bitLength = (long) originalLength * 8;

        /* ---------- Padding ---------- */
        int padLen = (56 - (originalLength + 1) % 64 + 64) % 64;  ///512 bits
        byte[] padded = new byte[originalLength + 1 + padLen + 8]; /////

        // Copy original message into padded array
        System.arraycopy(msg, 0, padded, 0, originalLength);

        // Append single '1' bit (10000000)
        padded[originalLength] = (byte) 0x80;

        // Append original length in bits (little-endian format)
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte)(bitLength >>> (8 * i)); ///////bit
        }

        /* ---------- Initial Values ---------- */
        // Standard MD5 initial buffer values (IV)
        int A = 0x67452301;
        int B = 0xefcdab89;
        int C = 0x98badcfe;
        int D = 0x10325476;

        // Total number of 512-bit blocks
        int totalBlocks = padded.length / 64;

        // Process each 512-bit block
        for (int block = 0; block < totalBlocks; block++) {

            System.out.println("\nBlock " + (block + 1));

            // Break block into 16 words (32-bit each)
            int[] X = new int[16];

            for (int j = 0; j < 16; j++) {
                int idx = block * 64 + j * 4;

                // Convert 4 bytes → 1 integer (little-endian)
                // & 0xff ensures unsigned byte handling
                X[j] = ((padded[idx] & 0xff)) |
                       ((padded[idx + 1] & 0xff) << 8) |
                       ((padded[idx + 2] & 0xff) << 16) |
                       ((padded[idx + 3] & 0xff) << 24);
            }

            // Initialize working variables for this block
            int a = A, b = B, c = C, d = D;

            // Main loop: 64 steps (4 rounds × 16 operations)
            for (int i = 0; i < 64; i++) {

                int g, k;

                // Round 1: uses AND, OR, NOT (basic nonlinear mixing)
                if (i < 16) {
                    g = (b & c) | (~b & d);   // Function result
                    k = i;                   // Direct index
                }
                // Round 2: changes bit dependency pattern
                else if (i < 32) {
                    g = (d & b) | (~d & c);
                    k = (5 * i + 1) % 16;
                }
                // Round 3: XOR-based mixing (strong diffusion)
                else if (i < 48) {
                    g = b ^ c ^ d;
                    k = (3 * i + 5) % 16;
                }
                // Round 4: final nonlinear transformation
                else {
                    g = c ^ (b | ~d);
                    k = (7 * i) % 16;
                }

                // Rotate variables (cyclic shift)
                int temp = d;
                d = c;
                c = b;

                // Core MD5 operation:
                // Combine previous value, function result, constant, and message word
                int sum = a + g + T[i] + X[k];

                // Left rotate and add to b (main mixing step)
                b = b + Integer.rotateLeft(sum, SHIFT[i]); //////

                // Update a
                a = temp;

                /* ---- Print at end of each round ---- */
                if (i == 15) {
                    System.out.printf("Round 1:\nA=%08x B=%08x C=%08x D=%08x\n", a,b,c,d); ///08
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

            // Add this block's result to overall hash state
            A += a;
            B += b;
            C += c;
            D += d;
        }

        // Final hash = concatenation of A, B, C, D (in hexadecimal)
        String finalHash = String.format("%08x%08x%08x%08x", A, B, C, D);

        System.out.println("\nFinal Hash Value: " + finalHash);
    }
}