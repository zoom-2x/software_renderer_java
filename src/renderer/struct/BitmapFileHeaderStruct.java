package renderer.struct;

public class BitmapFileHeaderStruct extends Struct
{
    public static final int STRUCT_BYTES = 14;

    public short type;
    public int size;
    public short res1;
    public short res2;
    public int offset;

    public BitmapFileHeaderStruct()
    {
        force_little_endian = true;

        _format.add(Struct.SHORT);
        _format.add(Struct.INT);
        _format.add(Struct.SHORT);
        _format.add(Struct.SHORT);
        _format.add(Struct.INT);
    }
}
