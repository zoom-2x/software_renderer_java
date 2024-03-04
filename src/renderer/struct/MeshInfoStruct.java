package renderer.struct;

public class MeshInfoStruct extends Struct
{
    public static final int STRUCT_BYTES = 64;

    public String name;
    public int pos_count;
    public int color_count;
    public int uv_count;
    public int normal_count;
    public int face_count;
    public long offset;
    public long bytes;
    public int attrs;
    public int total_tris;

    public MeshInfoStruct()
    {
        _format.add(Struct.ASCII20);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.LONG);
        _format.add(Struct.LONG);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
    }
}
