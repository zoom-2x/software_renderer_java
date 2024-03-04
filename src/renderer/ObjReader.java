package renderer;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import renderer.struct.MeshInfoStruct;

public class ObjReader
{
    public static Mesh load(String path) throws FileNotFoundException, IOException
    {
        Mesh mesh = null;
        BufferedReader file = null;

        if (path.isEmpty())
            return mesh;

        try
        {
            System.out.printf(" > packing: %s\n", path);

            file = new BufferedReader(new FileReader(path));
            String line = "";

            MeshInfoStruct info = new MeshInfoStruct();

            info.pos_count = 0;
            info.color_count = 0;
            info.uv_count = 0;
            info.normal_count = 0;
            info.face_count = 0;
            info.total_tris = 0;

            ArrayList<Float> pos_list = new ArrayList<Float>();
            ArrayList<Integer> color_list = new ArrayList<Integer>();
            ArrayList<Float> uv_list = new ArrayList<Float>();
            ArrayList<Float> normal_list = new ArrayList<Float>();
            ArrayList<Integer> face_list = new ArrayList<Integer>();

            while ((line = file.readLine()) != null)
            {
                int v_index = line.indexOf("v ");
                int vc_index = line.indexOf("vc ");
                int vt_index = line.indexOf("vt ");
                int vn_index = line.indexOf("vn ");
                int f_index = line.indexOf("f ");

                if (v_index > -1)
                {
                    String[] pos_data = line.substring(v_index + 2).split(" ");

                    for (int i = 0; i < pos_data.length; ++i)
                    {
                        info.pos_count++;
                        pos_list.add(Float.parseFloat(pos_data[i]));
                    }
                }

                else if (vc_index > -1)
                {
                    String[] color_data = line.substring(vc_index + 3).split(" ");

                    for (int i = 0; i < color_data.length; ++i)
                    {
                        info.color_count++;
                        color_list.add(Integer.valueOf(color_data[i]) & 0xff);
                    }
                }

                else if (vt_index > -1)
                {
                    String[] uv_data = line.substring(vt_index + 3).split(" ");

                    for (int i = 0; i < uv_data.length; ++i)
                    {
                        info.uv_count++;
                        uv_list.add(Float.valueOf(uv_data[i]));
                    }
                }

                else if (vn_index > -1)
                {
                    String[] normal_data = line.substring(vn_index + 3).split(" ");

                    for (int i = 0; i < normal_data.length; ++i)
                    {
                        info.normal_count++;
                        normal_list.add(Float.valueOf(normal_data[i]));
                    }
                }

                else if (f_index > -1)
                {
                    String[] face_data = line.substring(f_index + 2).split(" ");

                    for (int i = 0; i < face_data.length; ++i)
                    {
                        String[] face_components = face_data[i].split("/");

                        for (int j = 0; j < face_components.length; ++j)
                        {
                            info.face_count++;
                            face_list.add(Integer.valueOf(face_components[j]));
                        }
                    }
                }
            }

            if (info.pos_count > 0) info.attrs++;
            if (info.uv_count > 0) info.attrs++;
            if (info.normal_count > 0) info.attrs++;
            if (info.color_count > 0) info.attrs++;

            info.total_tris = info.face_count / (3 * info.attrs);

            mesh = new Mesh(info);

            for (int i = 0; i < pos_list.size(); ++i) {
                mesh.pos[i] = pos_list.get(i);
            }

            for (int i = 0; i < color_list.size(); ++i) {
                mesh.color[i] = (byte) (color_list.get(i) & 0xff);
            }

            for (int i = 0; i < uv_list.size(); ++i) {
                mesh.uv[i] = uv_list.get(i);
            }

            for (int i = 0; i < normal_list.size(); ++i) {
                mesh.normal[i] = normal_list.get(i);
            }

            for (int i = 0; i < face_list.size(); ++i) {
                mesh.face[i] = face_list.get(i);
            }
        }
        catch (FileNotFoundException e) {
            System.err.printf("\n > ObjReader: not found %s\n\n", path);
        }
        catch (NumberFormatException e)
        {
            System.err.printf("\n > ObjReader: wrong format in %s\n", path);
            System.err.printf(" > details: %s\n\n", e.getMessage());
        }
        catch(Exception e) {
            throw e;
        }
        finally
        {
            if (file != null)
            {
                // System.out.printf(" > ObjReader: closing file %s\n", path);
                file.close();
            }
        }

        return mesh;
    }
}
