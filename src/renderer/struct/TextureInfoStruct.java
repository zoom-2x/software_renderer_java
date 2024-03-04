package renderer.struct;

public class TextureInfoStruct extends Struct
{
    public static final int STRUCT_BYTES = 40;

    public String name;
    public int width;
    public int height;
    public int bytes;
    public long offset;

    public TextureInfoStruct()
    {
        _format.add(Struct.ASCII20);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.LONG);
    }
}
