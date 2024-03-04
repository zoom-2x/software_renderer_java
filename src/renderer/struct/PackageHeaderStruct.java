package renderer.struct;

public class PackageHeaderStruct extends Struct
{
    public static final int STRUCT_BYTES = 12;

    public int id;
    public int count;
    public int info_size;

    public PackageHeaderStruct()
    {
        _format.add(Struct.INT);
        _format.add(Struct.INT);
        _format.add(Struct.INT);
    }
}
