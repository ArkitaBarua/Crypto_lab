import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class aes_client_sender {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        System.out.print("Plaintext: ");
        String plaintext = sc.nextLine();
        System.out.print("Key: ");
        String key = sc.nextLine();

        String cipherStr = AES.encryptToString(plaintext, key);

        try (Socket s = new Socket("localhost", 5000);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {

            byte[] cipherUtf8 = cipherStr.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(cipherUtf8.length);
            dos.write(cipherUtf8);
            dos.flush();

            System.out.println("Cypher text sent: "+cipherStr);

            int respLen = dis.readInt();
            byte[] resp = new byte[respLen];
            dis.readFully(resp);
            System.out.println("Receiver response: " + new String(resp, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // AES (encrypt-only)
    static class AES {
        private static final int Nr = 10;
        private static final int[] SBOX = {
            0x63,0x7c,0x77,0x7b,0xf2,0x6b,0x6f,0xc5,0x30,0x01,0x67,0x2b,0xfe,0xd7,0xab,0x76,
            0xca,0x82,0xc9,0x7d,0xfa,0x59,0x47,0xf0,0xad,0xd4,0xa2,0xaf,0x9c,0xa4,0x72,0xc0,
            0xb7,0xfd,0x93,0x26,0x36,0x3f,0xf7,0xcc,0x34,0xa5,0xe5,0xf1,0x71,0xd8,0x31,0x15,
            0x04,0xc7,0x23,0xc3,0x18,0x96,0x05,0x9a,0x07,0x12,0x80,0xe2,0xeb,0x27,0xb2,0x75,
            0x09,0x83,0x2c,0x1a,0x1b,0x6e,0x5a,0xa0,0x52,0x3b,0xd6,0xb3,0x29,0xe3,0x2f,0x84,
            0x53,0xd1,0x00,0xed,0x20,0xfc,0xb1,0x5b,0x6a,0xcb,0xbe,0x39,0x4a,0x4c,0x58,0xcf,
            0xd0,0xef,0xaa,0xfb,0x43,0x4d,0x33,0x85,0x45,0xf9,0x02,0x7f,0x50,0x3c,0x9f,0xa8,
            0x51,0xa3,0x40,0x8f,0x92,0x9d,0x38,0xf5,0xbc,0xb6,0xda,0x21,0x10,0xff,0xf3,0xd2,
            0xcd,0x0c,0x13,0xec,0x5f,0x97,0x44,0x17,0xc4,0xa7,0x7e,0x3d,0x64,0x5d,0x19,0x73,
            0x60,0x81,0x4f,0xdc,0x22,0x2a,0x90,0x88,0x46,0xee,0xb8,0x14,0xde,0x5e,0x0b,0xdb,
            0xe0,0x32,0x3a,0x0a,0x49,0x06,0x24,0x5c,0xc2,0xd3,0xac,0x62,0x91,0x95,0xe4,0x79,
            0xe7,0xc8,0x37,0x6d,0x8d,0xd5,0x4e,0xa9,0x6c,0x56,0xf4,0xea,0x65,0x7a,0xae,0x08,
            0xba,0x78,0x25,0x2e,0x1c,0xa6,0xb4,0xc6,0xe8,0xdd,0x74,0x1f,0x4b,0xbd,0x8b,0x8a,
            0x70,0x3e,0xb5,0x66,0x48,0x03,0xf6,0x0e,0x61,0x35,0x57,0xb9,0x86,0xc1,0x1d,0x9e,
            0xe1,0xf8,0x98,0x11,0x69,0xd9,0x8e,0x94,0x9b,0x1e,0x87,0xe9,0xce,0x55,0x28,0xdf,
            0x8c,0xa1,0x89,0x0d,0xbf,0xe6,0x42,0x68,0x41,0x99,0x2d,0x0f,0xb0,0x54,0xbb,0x16
        };
        private static final int[] RCON = {0x00,0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80,0x1B,0x36};

        private static String bytesToHex(byte[] b) { StringBuilder sb=new StringBuilder(); for(byte x:b) sb.append(String.format("%02X", x&0xFF)); return sb.toString(); }
        private static String stateToHex(byte[][] s) { StringBuilder sb=new StringBuilder(); for(int r=0;r<4;r++){ for(int c=0;c<4;c++) sb.append(String.format("%02X", s[r][c]&0xFF)); if(r<3) sb.append(' ');} return sb.toString(); }
        private static byte[][] bytesToState(byte[] in){ byte[][] s=new byte[4][4]; for(int i=0;i<16;i++) s[i%4][i/4]=in[i]; return s; }
        private static byte[] stateToBytes(byte[][] state){ byte[] out=new byte[16]; for(int i=0;i<16;i++) out[i]=state[i%4][i/4]; return out; }

        private static int mul(int a,int b){ int res=0; int aa=a&0xFF, bb=b&0xFF; for(int i=0;i<8;i++){ if((bb&1)!=0) res^=aa; boolean hi=(aa&0x80)!=0; aa=(aa<<1)&0xFF; if(hi) aa^=0x1B; bb>>>=1; } return res; }

        private static void subBytes(byte[][] s){ for(int r=0;r<4;r++) for(int c=0;c<4;c++) s[r][c]=(byte)SBOX[s[r][c]&0xFF]; }
        private static void shiftRows(byte[][] s){ for(int r=1;r<4;r++){ byte[] tmp=new byte[4]; for(int c=0;c<4;c++) tmp[c]=s[r][(c+r)%4]; for(int c=0;c<4;c++) s[r][c]=tmp[c]; } }
        private static void mixColumns(byte[][] s){ for(int c=0;c<4;c++){ int a0=s[0][c]&0xFF,a1=s[1][c]&0xFF,a2=s[2][c]&0xFF,a3=s[3][c]&0xFF; int r0=mul(0x02,a0)^mul(0x03,a1)^a2^a3; int r1=a0^mul(0x02,a1)^mul(0x03,a2)^a3; int r2=a0^a1^mul(0x02,a2)^mul(0x03,a3); int r3=mul(0x03,a0)^a1^a2^mul(0x02,a3); s[0][c]=(byte)r0; s[1][c]=(byte)r1; s[2][c]=(byte)r2; s[3][c]=(byte)r3; } }
        private static void addRoundKey(byte[][] s, byte[] rk){ for(int c=0;c<4;c++) for(int r=0;r<4;r++) s[r][c]=(byte)((s[r][c]&0xFF)^(rk[c*4+r]&0xFF)); }

        private static byte[] expandKey(byte[] key){
            byte[] expanded=new byte[16*(Nr+1)]; System.arraycopy(key,0,expanded,0,16);
            int bytesGenerated=16; int rconIter=1; byte[] temp=new byte[4];
            while(bytesGenerated<expanded.length){
                for(int i=0;i<4;i++) temp[i]=expanded[bytesGenerated-4+i];
                if(bytesGenerated%16==0){
                    byte t=temp[0]; temp[0]=temp[1]; temp[1]=temp[2]; temp[2]=temp[3]; temp[3]=t;
                    for(int i=0;i<4;i++) temp[i]=(byte)SBOX[temp[i]&0xFF];
                    temp[0]=(byte)((temp[0]&0xFF)^RCON[rconIter]); rconIter++;
                }
                for(int i=0;i<4;i++){ expanded[bytesGenerated]=(byte)((expanded[bytesGenerated-16]&0xFF)^(temp[i]&0xFF)); bytesGenerated++; }
            }
            return expanded;
        }
        private static byte[] getRoundKey(byte[] expanded,int round){ byte[] rk=new byte[16]; System.arraycopy(expanded,round*16,rk,0,16); return rk; }

        private static byte[] encryptBlock(byte[] in, byte[] expandedKey, int blockIndex){
            byte[][] state = bytesToState(in);
            System.out.println("\n--- Encrypting block " + blockIndex + " ---");
            System.out.println("Initial State: " + stateToHex(state));
            addRoundKey(state, getRoundKey(expandedKey,0));
            System.out.println("After AddRoundKey (Round 0): " + stateToHex(state));
            for(int round=1; round<Nr; round++){
                subBytes(state); System.out.println("Round " + round + " - After SubBytes: " + stateToHex(state));
                shiftRows(state); System.out.println("Round " + round + " - After ShiftRows: " + stateToHex(state));
                mixColumns(state); System.out.println("Round " + round + " - After MixColumns: " + stateToHex(state));
                addRoundKey(state, getRoundKey(expandedKey,round)); System.out.println("Round " + round + " - After AddRoundKey: " + stateToHex(state));
            }
            subBytes(state); System.out.println("Round " + Nr + " - After SubBytes: " + stateToHex(state));
            shiftRows(state); System.out.println("Round " + Nr + " - After ShiftRows: " + stateToHex(state));
            addRoundKey(state, getRoundKey(expandedKey,Nr)); System.out.println("Round " + Nr + " - After AddRoundKey (Final): " + stateToHex(state));
            return stateToBytes(state);
        }

        public static byte[] encrypt(byte[] plaintext, byte[] key){
            int padLen = 16 - (plaintext.length % 16); if (padLen == 0) padLen = 16;
            byte[] padded = Arrays.copyOf(plaintext, plaintext.length + padLen);
            for (int i = plaintext.length; i < padded.length; i++) padded[i] = (byte) padLen;
            byte[] expandedKey = expandKey(key);
            byte[] out = new byte[padded.length]; int blocks = padded.length / 16;
            for (int i=0;i<blocks;i++){ byte[] block = Arrays.copyOfRange(padded, i*16, (i+1)*16); byte[] enc = encryptBlock(block, expandedKey, i); System.arraycopy(enc,0,out,i*16,16); }
            System.out.println("\nFinal Ciphertext (hex): " + bytesToHex(out));
            return out;
        }

        private static byte[] parseKeyInput(String keyInput){ byte[] kb = keyInput.getBytes(StandardCharsets.UTF_8); if(kb.length==16) return kb; if(kb.length<16){ byte[] p=new byte[16]; System.arraycopy(kb,0,p,0,kb.length); return p; } return Arrays.copyOf(kb,16); }

        public static String encryptToString(String plaintextStr, String keyStr){
            byte[] key = parseKeyInput(keyStr);
            byte[] plaintext = plaintextStr.getBytes(StandardCharsets.UTF_8);
            System.out.println("\n--- START ENCRYPTION ---");
            byte[] ciphertext = encrypt(plaintext, key);
            return new String(ciphertext, StandardCharsets.ISO_8859_1);
        }
    }
}
