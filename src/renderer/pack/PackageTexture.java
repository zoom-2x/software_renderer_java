package renderer.pack;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import renderer.struct.TextureInfoStruct;
import renderer.struct.PackageHeaderStruct;
import renderer.Texture;
import renderer.BitmapReader;

public class PackageTexture
{
    protected String output_file = null;
    protected ArrayList<String> name = new ArrayList<String>();
    protected ArrayList<String> list = new ArrayList<String>();

    public PackageTexture(String texture_config_path)
    {
        BufferedReader stream = null;

        try
        {
            String line = "";
            int index = 0;
            stream = new BufferedReader(new FileReader(texture_config_path));

            while ((line = stream.readLine()) != null)
            {
                // The first element represents the output package path.
                if (index == 0)
                    output_file = line;
                else
                {
                    String[] parts = line.split(" ");

                    if (parts.length == 2)
                    {
                        name.add(parts[0]);
                        list.add(parts[1]);
                    }
                }

                index++;
            }
        }
        catch (FileNotFoundException e) {
            System.out.printf(" > PackageTexture: file not found %s\n", texture_config_path);
        }
        catch (IOException e) {
            System.out.printf(" > PackageTexture: error %s\n", texture_config_path);
        }
        finally
        {
            if (stream != null)
            {
                try { stream.close(); }
                catch (IOException e) { System.out.println(" > PackageTexture: cannot close stream"); }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------
    // -- Package format.
    // ----------------------------------------------------------------------------------------------
    // [id][header_size][count][info]...[info][data]...[data]
    // id = 4 bytes
    // texture_count = 4 bytes
    // info_size = 4 bytes
    // info = [width:4][height:4][bytes:4][offset:8] = 20 bytes
    // data = info[bytes]
    // ----------------------------------------------------------------------------------------------

    public void generate()
    {
        RandomAccessFile out = null;

        if (output_file == null)
        {
            System.out.println(" > PackageTexture: missing package output file");
            return;
        }

        try
        {
            out = new RandomAccessFile(output_file, "rw");

            int texture_count = list.size();
            int valid_count = 0;
            int info_index = 0;

            PackageHeaderStruct header_struct = new PackageHeaderStruct();
            TextureInfoStruct[] info = new TextureInfoStruct[texture_count];

            long header_bytes = PackageHeaderStruct.STRUCT_BYTES +
                                TextureInfoStruct.STRUCT_BYTES * texture_count;

            long texture_offset_pointer = header_bytes;

            // ------------------------------------------------------------------------------
            // -- Image data.
            // ------------------------------------------------------------------------------

            out.seek(header_bytes);

            for (int i = 0; i < texture_count; ++i)
            {
                try
                {
                    Texture texture = BitmapReader.load(list.get(i));

                    if (texture != null)
                    {
                        texture.info.name = name.get(i);
                        texture.info.offset = texture_offset_pointer;
                        info[info_index++] = texture.info;
                        texture_offset_pointer += texture.info.bytes;

                        out.write(texture.data, 0, texture.data.length);

                        valid_count++;
                    }
                }
                catch (FileNotFoundException e) {
                    System.err.printf(" > PackageTexture: texture not found %s\n", list.get(i));
                }
            }

            // ------------------------------------------------------------------------------
            // -- Header data.
            // ------------------------------------------------------------------------------

            header_struct.id = 'P' << 24 | 'A' << 16 | 'K' << 8 | 'T';
            header_struct.count = valid_count;
            header_struct.info_size = header_struct.get_bytes();

            out.seek(0);
            header_struct.write(out);

            // Write the texture info.
            for (int i = 0; i < valid_count; ++i) {
                info[i].write(out);
            }
        }
        catch (FileNotFoundException e) {
            System.err.printf(" > PackageTexture: not found %f\n", output_file);
        }
        catch (IOException e) {
            System.err.println(" > PackageTexture: error");
        }
        finally
        {
            if (out != null)
            {
                try { out.close(); }
                catch (IOException e) { System.out.println(" > PackageTexture: cannot close stream"); }
            }

        }
    }
}
