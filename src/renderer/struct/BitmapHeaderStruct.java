package renderer.struct;

public class BitmapHeaderStruct extends Struct
{
    // Partial header fields.
    public static final int STRUCT_BYTES = 24;

    public int size;
    public int width;
    public int height;
    public short planes;
    public short bits_per_pixel;
    public int compression;
    public int size_of_bitmap;

    public BitmapHeaderStruct()
    {
        force_little_endian = true;

        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.SHORT);
        _format.add(Struct.SHORT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
    }
}
