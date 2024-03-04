package renderer;

public class Scanline
{
    public int count;

    // z, w, attrs ...
    public float[] interp_base;
    public float[] interp;

    public Scanline(int attr_count)
    {
        count = 2 + attr_count;

        interp_base = new float[count];
        interp = new float[count];
    }
}
