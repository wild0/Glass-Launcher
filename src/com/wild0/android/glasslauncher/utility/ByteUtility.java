package com.wild0.android.glasslauncher.utility;

/**
 * Created by roy on 2015/4/8.
 */
public class ByteUtility {

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];

        result[0] = (byte) ((value & 0xFF000000) >> 24);
        result[1] = (byte) ((value & 0x00FF0000) >> 16);
        result[2] = (byte) ((value & 0x0000FF00) >> 8);
        result[3] = (byte) ((value & 0x000000FF) >> 0);

        return result;
    }
    public static int bytesToInt(byte[] b)
    {
        int value= 0;
        for(int i=0; i<b.length; i++)
            value = (value << 8) | b[i];
        return value;
    }


}
