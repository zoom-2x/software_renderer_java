package renderer;

import renderer.program.ProgramBase;

public class ScanlineTaskData
{
    public int count;

    public Triangle triangle;
    public Gradients gradients;
    public Sampler sampler;
    public ProgramBase program;

    public int sx;
    public int ex;
    public int y;

    // z, w, attrs ...
    public float[] interp_base;
    public float[] interp;

    public ScanlineTaskData(int attr_count)
    {
        count = 2 + attr_count;

        interp_base = new float[count];
        interp = new float[count];
    }
}
