package renderer.pack;

import java.io.RandomAccessFile;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import renderer.Assets;
import renderer.Texture;
import renderer.Mesh;
import renderer.struct.PackageHeaderStruct;
import renderer.struct.TextureInfoStruct;
import renderer.struct.MeshInfoStruct;

public class PackageLoader
{
    public static void load_textures(Assets assets, String path)
    {
        DataInputStream file = null;

        try
        {
            // file = new RandomAccessFile(path, "r");
            file = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));

            PackageHeaderStruct package_header = new PackageHeaderStruct();
            package_header.read(file);

            int id_check = ('P' << 24) | ('A' << 16) | ('K' << 8) | 'T';

            if (package_header.id == id_check)
            {
                if (package_header.count > 0)
                {
                    Texture[] textures = new Texture[package_header.count];

                    for (int i = 0; i < package_header.count; ++i)
                    {
                        TextureInfoStruct info = new TextureInfoStruct();
                        info.read(file);

                        textures[i] = new Texture(info);
                    }

                    for (int i = 0; i < package_header.count; ++i)
                    {
                        Texture current_texture = textures[i];

                        System.out.printf(" > Loading texture: %s\n", current_texture.info.name);

                        // file.seek(current_texture.info.offset);
                        file.read(current_texture.data);
                    }

                    for (int i = 0; i < textures.length; ++i)
                    {
                        String name = textures[i].info.name;
                        assets.textures.put(name, textures[i]);
                    }
                }
            }
            else
                System.out.printf(" > PackageLoader: invalid package %s\n", path);
        }
        catch(FileNotFoundException e) {
            System.out.printf(" > PackageLoader: package not found %s\n", path);
        }
        catch(IOException e) {
            System.out.printf(" > PackageLoader: error reading the package %s\n", path);
        }
        finally
        {
            if (file != null)
            {
                // System.out.printf(" > PackageLoader: closing file %s\n", path);
                try { file.close(); } catch (IOException e) {}
            }
        }
    }

    public static void load_meshes(Assets assets, String path)
    {
        // RandomAccessFile file = null;
        DataInputStream file = null;

        try
        {
            // file = new RandomAccessFile(path, "r");
            file = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));

            PackageHeaderStruct package_header = new PackageHeaderStruct();
            package_header.read(file);

            int id_check = ('P' << 24) | ('A' << 16) | ('K' << 8) | 'M';

            if (package_header.id == id_check)
            {
                if (package_header.count > 0)
                {
                    Mesh[] meshes = new Mesh[package_header.count];

                    for (int i = 0; i < package_header.count; ++i)
                    {
                        MeshInfoStruct info = new MeshInfoStruct();
                        info.read(file);

                        meshes[i] = new Mesh(info);
                    }

                    for (int i = 0; i < package_header.count; ++i)
                    {
                        Mesh current_mesh = meshes[i];
                        // file.seek(current_mesh.info.offset);

                        System.out.printf(" > Loading mesh: %s\n", current_mesh.info.name);

                        for (int j = 0; j < current_mesh.info.pos_count; ++j) {
                            current_mesh.pos[j] = file.readFloat();
                        }

                        for (int j = 0; j < current_mesh.info.color_count; ++j) {
                            current_mesh.color[j] = file.readByte();
                        }

                        for (int j = 0; j < current_mesh.info.uv_count; ++j) {
                            current_mesh.uv[j] = file.readFloat();
                        }

                        for (int j = 0; j < current_mesh.info.normal_count; ++j) {
                            current_mesh.normal[j] = file.readFloat();
                        }

                        for (int j = 0; j < current_mesh.info.face_count; ++j) {
                            current_mesh.face[j] = file.readInt();
                        }
                    }

                    for (int i = 0; i < meshes.length; ++i)
                    {
                        String name = meshes[i].info.name;
                        assets.meshes.put(meshes[i].info.name, meshes[i]);
                    }
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.printf(" > PackageLoader: package not found %s\n", path);
        }
        catch(IOException e) {
            System.out.printf(" > PackageLoader: error reading the package %s\n", path);
        }
        finally
        {
            if (file != null)
            {
                // System.out.printf(" > PackageLoader: closing file %s\n", path);
                try { file.close(); } catch (IOException e) {}
            }
        }
    }
}
