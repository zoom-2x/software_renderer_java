package renderer;

import java.nio.charset.StandardCharsets;

public class Utils
{
    public static byte[] string_to_ascii(String s, int max_len)
    {
        byte[] buffer = new byte[max_len];
        byte[] string_buffer = s.getBytes(StandardCharsets.US_ASCII);

        int end_index = string_buffer.length > max_len ? max_len : string_buffer.length;

        for (int i = 0; i < end_index; ++i) {
            buffer[i] = string_buffer[i];
        }

        return buffer;
    }

    public static String ascii_to_string(byte[] buffer) {
        return new String(buffer);
    }
}
