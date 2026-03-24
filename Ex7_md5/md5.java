import java.util.Scanner;

public class md5 {

    /* ---------- Shift amounts ---------- */
    // These are predefined rotation amounts used in each of the 64 steps
    // MD5 uses left rotation to mix bits (important for diffusion)
    private static final int[] SHIFT = {
        7,12,17,22, 7,12,17,22, 7,12,17,22, 7,12,17,22,
        5,9,14,20, 5,9,14,20, 5,9,14,20, 5,9,14,20,
        4,11,16,23, 4,11,16,23, 4,11,16,23, 4,11,16,23,
        6,10,15,21, 6,10,15,21, 6,10,15,21, 6,10,15,21
    };

    /* ---------- Constants ---------- */
    // T[i] = floor(2^32 × abs(sin(i+1)))
    // These are "nothing-up-my-sleeve" constants to avoid bias
    private static final int[] T = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            // Precompute constants using sine function
            T[i] = (int)((1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Input: ");
        String input = sc.nextLine();

        // Print input length (just for debugging/understanding)
        System.out.println("Number of characters in input: " + input.length());

        // Convert input string into byte array
        byte[] msg = input.getBytes();
        int originalLength = msg.length;

        // Length of message in bits (required for padding)
        long bitLength = (long) originalLength * 8;

        /* ---------- Padding ---------- */
        // MD5 requires message length ≡ 448 mod 512 (i.e., 56 bytes mod 64)
        int padLen = (56 - (originalLength + 1) % 64 + 64) % 64;

        // Total new array: original + 1 byte (0x80) + padding + 8 bytes (length)
        byte[] padded = new byte[originalLength + 1 + padLen + 8];

        // Copy original message
        System.arraycopy(msg, 0, padded, 0, originalLength);

        // Append '1' bit (10000000 in binary)
        padded[originalLength] = (byte) 0x80;

        // Append original length in bits (little-endian format)
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte)(bitLength >>> (8 * i));
        }

        /* ---------- Initial Values ---------- */
        // These are standard MD5 initial buffer values (IV)
        int A = 0x67452301;
        int B = 0xefcdab89;
        int C = 0x98badcfe;
        int D = 0x10325476;

        // Number of 512-bit blocks (64 bytes each)
        int totalBlocks = padded.length / 64;

        // Process each 512-bit block
        for (int block = 0; block < totalBlocks; block++) {

            System.out.println("\nBlock " + (block + 1));

            // Break block into 16 words (32 bits each)
            int[] X = new int[16];

            for (int j = 0; j < 16; j++) {
                int idx = block * 64 + j * 4;

                // Convert 4 bytes → 1 int (little-endian)
                X[j] = ((padded[idx] & 0xff)) |
                       ((padded[idx + 1] & 0xff) << 8) |
                       ((padded[idx + 2] & 0xff) << 16) |
                       ((padded[idx + 3] & 0xff) << 24);
            }

            // Initialize working variables for this block
            int a = A, b = B, c = C, d = D;

            // Main loop: 64 operations (4 rounds × 16 steps)
            for (int i = 0; i < 64; i++) {

                int F, g;

                // Round 1
                if (i < 16) {
                    F = (b & c) | (~b & d);   // Non-linear function
                    g = i;                   // Direct index
                }
                // Round 2
                else if (i < 32) {
                    F = (d & b) | (~d & c);
                    g = (5 * i + 1) % 16;    // Different message access pattern
                }
                // Round 3
                else if (i < 48) {
                    F = b ^ c ^ d;           // XOR-based mixing
                    g = (3 * i + 5) % 16;
                }
                // Round 4
                else {
                    F = c ^ (b | ~d);
                    g = (7 * i) % 16;
                }

                // Rotate variables (cyclic shift of a, b, c, d)
                int temp = d;
                d = c;
                c = b;

                // Core MD5 operation
                int sum = a + F + T[i] + X[g];

                // Left rotate and add to b
                b = b + Integer.rotateLeft(sum, SHIFT[i]);

                // Update a
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

            // Add this block's result to global state
            A += a;
            B += b;
            C += c;
            D += d;
        }

        // Final hash = concatenation of A, B, C, D (in hex)
        String finalHash = String.format("%08x%08x%08x%08x", A, B, C, D);

        System.out.println("\nFinal Hash Value: " + finalHash);
    }
}