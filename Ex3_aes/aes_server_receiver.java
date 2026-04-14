import java.io.*; 
import java.net.ServerSocket; 
import java.net.Socket; 
import java.nio.charset.StandardCharsets; 
import java.util.Arrays; 
import java.util.Scanner; 

public class aes_server_receiver { 

    public static void main(String[] args) throws IOException{ 

        // Port number where server listens
        int port = 5000; 
        System.out.println("Receiver listening on port " + port); 

        ServerSocket ss = new ServerSocket(port);

            // Wait for client connection
        Socket s = ss.accept(); 
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

        // Read ciphertext length
        int cipherLen = dis.readInt(); 

        // Allocate array for ciphertext
        byte[] cipherBytes = new byte[cipherLen]; 

        // Read full ciphertext
        dis.readFully(cipherBytes); 

        // Convert to string
        String cipherStr = new String(cipherBytes, StandardCharsets.UTF_8); 
        System.out.println("Cypher received: " + cipherStr); 

        // Ask for decryption key
        System.out.println("Ciphertext received. Please enter key to decrypt:"); 
        Scanner sc = new Scanner(System.in, "UTF-8"); 
        String key = sc.nextLine(); 

        // Decrypt ciphertext
        String plaintext = AES.decryptFromString(cipherStr, key); 

        // Print plaintext
        System.out.println("\nDecrypted plaintext: " + plaintext); 

        // Prepare acknowledgment
        String ack = "OK: decrypted " + plaintext.length() + " chars"; 

        byte[] ackBytes = ack.getBytes(StandardCharsets.UTF_8); 

        // Send response length
        dos.writeInt(ackBytes.length); 

        // Send response
        dos.write(ackBytes); 
        dos.flush();  
    } 

    // ================= AES DECRYPTION CLASS =================
    static class AES { 

        // Number of AES rounds (AES-128)
        private static final int Nr = 10; 

        // Inverse S-Box
        private static final int[] INV_SBOX = { 
            0x52,0x09,0x6A,0xD5,0x30,0x36,0xA5,0x38,0xBF,0x40,0xA3,0x9E,0x81,0xF3,0xD7,0xFB, 
            0x7C,0xE3,0x39,0x82,0x9B,0x2F,0xFF,0x87,0x34,0x8E,0x43,0x44,0xC4,0xDE,0xE9,0xCB, 
            0x54,0x7B,0x94,0x32,0xA6,0xC2,0x23,0x3D,0xEE,0x4C,0x95,0x0B,0x42,0xFA,0xC3,0x4E, 
            0x08,0x2E,0xA1,0x66,0x28,0xD9,0x24,0xB2,0x76,0x5B,0xA2,0x49,0x6D,0x8B,0xD1,0x25, 
            0x72,0xF8,0xF6,0x64,0x86,0x68,0x98,0x16,0xD4,0xA4,0x5C,0xCC,0x5D,0x65,0xB6,0x92, 
            0x6C,0x70,0x48,0x50,0xFD,0xED,0xB9,0xDA,0x5E,0x15,0x46,0x57,0xA7,0x8D,0x9D,0x84, 
            0x90,0xD8,0xAB,0x00,0x8C,0xBC,0xD3,0x0A,0xF7,0xE4,0x58,0x05,0xB8,0xB3,0x45,0x06, 
            0xD0,0x2C,0x1E,0x8F,0xCA,0x3F,0x0F,0x02,0xC1,0xAF,0xBD,0x03,0x01,0x13,0x8A,0x6B, 
            0x3A,0x91,0x11,0x41,0x4F,0x67,0xDC,0xEA,0x97,0xF2,0xCF,0xCE,0xF0,0xB4,0xE6,0x73, 
            0x96,0xAC,0x74,0x22,0xE7,0xAD,0x35,0x85,0xE2,0xF9,0x37,0xE8,0x1C,0x75,0xDF,0x6E, 
            0x47,0xF1,0x1A,0x71,0x1D,0x29,0xC5,0x89,0x6F,0xB7,0x62,0x0E,0xAA,0x18,0xBE,0x1B, 
            0xFC,0x56,0x3E,0x4B,0xC6,0xD2,0x79,0x20,0x9A,0xDB,0xC0,0xFE,0x78,0xCD,0x5A,0xF4, 
            0x1F,0xDD,0xA8,0x33,0x88,0x07,0xC7,0x31,0xB1,0x12,0x10,0x59,0x27,0x80,0xEC,0x5F, 
            0x60,0x51,0x7F,0xA9,0x19,0xB5,0x4A,0x0D,0x2D,0xE5,0x7A,0x9F,0x93,0xC9,0x9C,0xEF, 
            0xA0,0xE0,0x3B,0x4D,0xAE,0x2A,0xF5,0xB0,0xC8,0xEB,0xBB,0x3C,0x83,0x53,0x99,0x61, 
            0x17,0x2B,0x04,0x7E,0xBA,0x77,0xD6,0x26,0xE1,0x69,0x14,0x63,0x55,0x21,0x0C,0x7D 
        }; 

        // SBOX for key expansion
        private static final int[] SBOX = { /* same as sender */ }; 

        // Round constants
        private static final int[] RCON = {0x00,0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80,0x1B,0x36}; 

        // Convert byte array to state matrix
        private static byte[][] bytesToState(byte[] in){ 
            byte[][] s=new byte[4][4]; 
            for(int i=0;i<16;i++) s[i%4][i/4]=in[i]; 
            return s; 
        } 

        // Convert state to byte array
        private static byte[] stateToBytes(byte[][] state){ 
            byte[] out=new byte[16]; 
            for(int i=0;i<16;i++) out[i]=state[i%4][i/4]; 
            return out; 
        } 

        // GF multiplication
        private static int mul(int a,int b){ 
            int res=0; int aa=a&0xFF, bb=b&0xFF; 
            for(int i=0;i<8;i++){ 
                if((bb&1)!=0) res^=aa; 
                boolean hi=(aa&0x80)!=0; 
                aa=(aa<<1)&0xFF; 
                if(hi) aa^=0x1B; 
                bb>>>=1; 
            } 
            return res; 
        } 

        // Inverse SubBytes
        private static void invSubBytes(byte[][] s){ 
            for(int r=0;r<4;r++) 
                for(int c=0;c<4;c++) 
                    s[r][c]=(byte)INV_SBOX[s[r][c]&0xFF]; 
        } 

        // Inverse ShiftRows
        private static void invShiftRows(byte[][] s){ 
            for(int r=1;r<4;r++){ 
                byte[] tmp=new byte[4]; 
                for(int c=0;c<4;c++) tmp[(c+r)%4]=s[r][c]; 
                for(int c=0;c<4;c++) s[r][c]=tmp[c]; 
            } 
        } 

        // Inverse MixColumns
        private static void invMixColumns(byte[][] s){ 
            for(int c=0;c<4;c++){ 
                int a0=s[0][c]&0xFF,a1=s[1][c]&0xFF,a2=s[2][c]&0xFF,a3=s[3][c]&0xFF; 
                int r0=mul(0x0E,a0)^mul(0x0B,a1)^mul(0x0D,a2)^mul(0x09,a3); 
                int r1=mul(0x09,a0)^mul(0x0E,a1)^mul(0x0B,a2)^mul(0x0D,a3); 
                int r2=mul(0x0D,a0)^mul(0x09,a1)^mul(0x0E,a2)^mul(0x0B,a3); 
                int r3=mul(0x0B,a0)^mul(0x0D,a1)^mul(0x09,a2)^mul(0x0E,a3); 
                s[0][c]=(byte)r0; 
                s[1][c]=(byte)r1; 
                s[2][c]=(byte)r2; 
                s[3][c]=(byte)r3; 
            } 
        } 

        // AddRoundKey
        private static void addRoundKey(byte[][] s, byte[] rk){ 
            for(int c=0;c<4;c++) 
                for(int r=0;r<4;r++) 
                    s[r][c]=(byte)((s[r][c]&0xFF)^(rk[c*4+r]&0xFF)); 
        } 

        // Key expansion
        private static byte[] expandKey(byte[] key){ 
            byte[] expanded=new byte[16*(Nr+1)]; 
            System.arraycopy(key,0,expanded,0,16); 
            int bytesGenerated=16; int rconIter=1; byte[] temp=new byte[4]; 
            while(bytesGenerated<expanded.length){ 
                for(int i=0;i<4;i++) temp[i]=expanded[bytesGenerated-4+i]; 
                if(bytesGenerated%16==0){ 
                    byte t=temp[0]; temp[0]=temp[1]; temp[1]=temp[2]; temp[2]=temp[3]; temp[3]=t; 
                    for(int i=0;i<4;i++) temp[i]=(byte)SBOX[temp[i]&0xFF]; 
                    temp[0]=(byte)((temp[0]&0xFF)^RCON[rconIter]); rconIter++; 
                } 
                for(int i=0;i<4;i++){ 
                    expanded[bytesGenerated]=(byte)((expanded[bytesGenerated-16]&0xFF)^(temp[i]&0xFF)); 
                    bytesGenerated++; 
                } 
            } 
            return expanded; 
        } 

        // Get round key
        private static byte[] getRoundKey(byte[] expanded,int round){ 
            byte[] rk=new byte[16]; 
            System.arraycopy(expanded,round*16,rk,0,16); 
            return rk; 
        } 

        // Decrypt one block
        private static byte[] decryptBlock(byte[] in, byte[] expandedKey, int blockIndex){ 
            byte[][] state = bytesToState(in); 

            System.out.println("\n--- Decrypting block " + blockIndex + " ---"); 

            // Initial AddRoundKey
            addRoundKey(state, getRoundKey(expandedKey, Nr)); 

            // Main rounds
            for (int round = Nr-1; round >= 1; round--) { 
                invShiftRows(state); 
                invSubBytes(state); 
                addRoundKey(state, getRoundKey(expandedKey, round)); 
                invMixColumns(state); 
            } 

            // Final round
            invShiftRows(state); 
            invSubBytes(state); 
            addRoundKey(state, getRoundKey(expandedKey, 0)); 

            return stateToBytes(state); 
        } 

        // Full decryption
        public static byte[] decrypt(byte[] ciphertext, byte[] key) { 
            byte[] expandedKey = expandKey(key); 
            byte[] out = new byte[ciphertext.length]; 
            int blocks = ciphertext.length / 16; 

            for (int i = 0; i < blocks; i++) { 
                byte[] block = Arrays.copyOfRange(ciphertext, i * 16, (i+1) * 16); 
                byte[] dec = decryptBlock(block, expandedKey, i); 
                System.arraycopy(dec, 0, out, i*16, 16); 
            } 

            // Remove padding
            int padLen = out[out.length - 1] & 0xFF; 
            return Arrays.copyOf(out, out.length - padLen); 
        } 

        // Convert key to 16 bytes
        private static byte[] parseKeyInput(String keyInput){ 
            byte[] kb = keyInput.getBytes(StandardCharsets.UTF_8); 
            if(kb.length==16) return kb; 
            if(kb.length<16){ byte[] p=new byte[16]; System.arraycopy(kb,0,p,0,kb.length); return p; } 
            return Arrays.copyOf(kb,16); 
        } 

        // Main decrypt function
        public static String decryptFromString(String cipherString, String keyStr){ 
            byte[] key = parseKeyInput(keyStr); 
            byte[] ciphertext = cipherString.getBytes(StandardCharsets.ISO_8859_1); 
            byte[] recovered = decrypt(ciphertext, key); 
            return new String(recovered, StandardCharsets.UTF_8); 
        } 
    } 
}