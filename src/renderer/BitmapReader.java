package renderer;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

import renderer.struct.BitmapFileHeaderStruct;
import renderer.struct.BitmapHeaderStruct;
import renderer.struct.TextureInfoStruct;

public class BitmapReader
{
    public static Texture load(String path) throws FileNotFoundException, IOException
    {
        Texture texture = null;
        RandomAccessFile file = null;

        if (path.isEmpty())
            return texture;

        try
        {
            System.out.printf(" > packing: %s\n", path);

            BitmapFileHeaderStruct bitmap_fileheader = new BitmapFileHeaderStruct();
            BitmapHeaderStruct bitmap_header = new BitmapHeaderStruct();

            file = new RandomAccessFile(path, "r");

            bitmap_fileheader.read(file);
            bitmap_header.read(file);

            if (bitmap_fileheader.type == 0x4d42)
            {
                boolean has_alpha = bitmap_header.bits_per_pixel == 32;

                if (bitmap_header.bits_per_pixel >= 24)
                {
                    int src_stride = has_alpha ? bitmap_header.width << 2 : bitmap_header.width * 3;
                    int dest_stride = bitmap_header.width << 2;

                    byte[] tmp_buffer = new byte[bitmap_header.size_of_bitmap];

                    TextureInfoStruct info = new TextureInfoStruct();

                    info.width = bitmap_header.width;
                    info.height = bitmap_header.height;
                    info.bytes = (bitmap_header.width * bitmap_header.height) << 2;
                    info.offset = 0;

                    texture = new Texture(info);

                    // Read the bitmap data.
                    file.seek(bitmap_fileheader.offset);
                    file.read(tmp_buffer);

                    int src_pointer = (bitmap_header.height - 1) * src_stride;
                    int dest_pointer = 0;

                    // Mirror the bitmap by height.
                    for (int y = 0; y < bitmap_header.height; ++y)
                    {
                        int src_pixel = src_pointer;
                        int dest_pixel = dest_pointer;

                        for (int x = 0; x < bitmap_header.width; ++x)
                        {
                            // In format: BGRA.
                            // file format: RGBA

                            if (has_alpha)
                            {
                                texture.data[dest_pixel + 0] = tmp_buffer[src_pixel + 2];
                                texture.data[dest_pixel + 1] = tmp_buffer[src_pixel + 1];
                                texture.data[dest_pixel + 2] = tmp_buffer[src_pixel + 0];
                                texture.data[dest_pixel + 3] = tmp_buffer[src_pixel + 3];

                                dest_pixel += 4;
                                src_pixel += 4;
                            }
                            else
                            {
                                texture.data[dest_pixel + 0] = tmp_buffer[src_pixel + 2];
                                texture.data[dest_pixel + 1] = tmp_buffer[src_pixel + 1];
                                texture.data[dest_pixel + 2] = tmp_buffer[src_pixel + 0];
                                texture.data[dest_pixel + 3] = (byte) 255;

                                dest_pixel += 4;
                                src_pixel += 3;
                            }
                        }

                        src_pointer -= src_stride;
                        dest_pointer += dest_stride;
                    }
                }
                else
                    System.err.printf(" > BitmapReader: invalid bitmap format %s\n", path);
            }
            else
                System.err.printf(" > BitmapReader: invalid bitmap file %s\n", path);
        }
        catch (Exception e) {
            throw e;
        }
        finally
        {
            if (file != null)
            {
                // System.out.printf(" > BitmapReader: closing file %s\n", path);
                file.close();
            }
        }

        return texture;
    }
}
