package renderer;

import renderer.struct.MeshInfoStruct;

public class Mesh
{
    public MeshInfoStruct info = null;

    public float[] pos;
    public byte[] color;
    public float[] uv;
    public float[] normal;
    public int[] face;

    public int face_components;
    public int pointer;

    // public Mesh(int pos_count, int color_count, int uv_count, int normal_count, int face_count)
    public Mesh(MeshInfoStruct info)
    {
        this.info = info;

        face_components = 0;
        pointer = 0;

        if (info.pos_count > 0)
        {
            pos = new float[info.pos_count];
            info.bytes += pos.length * 4;
            face_components++;
        }

        // Color format: rgb.
        if (info.color_count > 0)
        {
            color = new byte[info.color_count];
            info.bytes += color.length * 1;
            face_components++;
        }

        if (info.uv_count > 0)
        {
            uv = new float[info.uv_count];
            info.bytes += uv.length * 4;
            face_components++;
        }

        if (info.normal_count > 0)
        {
            normal = new float[info.normal_count];
            info.bytes += normal.length * 4;
            face_components++;
        }

        if (info.face_count > 0)
        {
            face = new int[info.face_count];
            info.bytes += face.length * 4;
        }
    }
}
