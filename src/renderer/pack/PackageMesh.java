package renderer.pack;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import renderer.struct.PackageHeaderStruct;
import renderer.struct.MeshInfoStruct;

import renderer.Mesh;
import renderer.ObjReader;

public class PackageMesh
{
    protected String output_file = "assets/meshes.pak";
    protected ArrayList<String> name = new ArrayList<String>();
    protected ArrayList<String> list = new ArrayList<String>();

    public PackageMesh(String mesh_config_path)
    {
        BufferedReader stream = null;

        try
        {
            String line = "";
            int index = 0;
            stream = new BufferedReader(new FileReader(mesh_config_path));

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
            System.out.printf(" > PackageMesh: file not found %s\n", mesh_config_path);
        }
        catch (IOException e) {
            System.out.printf(" > PackageMesh: error %s\n", mesh_config_path);
        }
        finally
        {
            if (stream != null)
            {
                try { stream.close(); }
                catch (IOException e) { System.out.println(" > PackageMesh: cannot close stream"); }
            }
        }
    }

    public void generate()
    {
        RandomAccessFile out = null;

        if (output_file == null)
        {
            System.out.println(" > PackageMesh: missing package output file");
            return;
        }

        try
        {
            File file = new File(output_file);

            if (file.exists())
                file.delete();

            out = new RandomAccessFile(file, "rw");

            int mesh_count = list.size();
            int valid_count = 0;

            PackageHeaderStruct header_struct = new PackageHeaderStruct();
            ArrayList<MeshInfoStruct> info_list = new ArrayList<MeshInfoStruct>();

            long header_bytes = PackageHeaderStruct.STRUCT_BYTES +
                                MeshInfoStruct.STRUCT_BYTES * mesh_count;

            long mesh_offset_bytes = header_bytes;

            // ------------------------------------------------------------------------------
            // -- Mesh data.
            // ------------------------------------------------------------------------------

            out.seek(header_bytes);

            for (int i = 0; i < mesh_count; ++i)
            {
                Mesh mesh = ObjReader.load(list.get(i));

                if (mesh != null)
                {
                    mesh.info.name = name.get(i);
                    info_list.add(mesh.info);

                    mesh.info.offset = mesh_offset_bytes;
                    mesh_offset_bytes += mesh.info.bytes;

                    // Vertex.
                    if (mesh.info.pos_count > 0)
                    {
                        for (int j = 0; j < mesh.pos.length; ++j) {
                            out.writeFloat(mesh.pos[j]);
                        }
                    }

                    // Color.
                    if (mesh.info.color_count > 0)
                    {
                        for (int j = 0; j < mesh.color.length; ++j) {
                            out.writeByte(mesh.color[j]);
                        }
                    }

                    // UV.
                    if (mesh.info.uv_count > 0)
                    {
                        for (int j = 0; j < mesh.uv.length; ++j) {
                            out.writeFloat(mesh.uv[j]);
                        }
                    }

                    // Normal.
                    if (mesh.info.normal_count > 0)
                    {
                        for (int j = 0; j < mesh.normal.length; ++j) {
                            out.writeFloat(mesh.normal[j]);
                        }
                    }

                    // Face.
                    if (mesh.info.face_count > 0)
                    {
                        for (int j = 0; j < mesh.face.length; ++j) {
                            out.writeInt(mesh.face[j]);
                        }
                    }

                    valid_count++;
                }
            }

            // ------------------------------------------------------------------------------
            // -- Header data.
            // ------------------------------------------------------------------------------

            header_struct.id = ('P' << 24) | ('A' << 16) | ('K' << 8) | 'M';
            header_struct.count = valid_count;
            header_struct.info_size = MeshInfoStruct.STRUCT_BYTES * mesh_count;

            out.seek(0);
            header_struct.write(out);

            // Write the mesh info.
            for (int i = 0; i < info_list.size(); ++i) {
                info_list.get(i).write(out);
            }
        }
        catch (FileNotFoundException e) {
            System.err.printf(" > PackageTexture: not found %s\n", output_file);
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
