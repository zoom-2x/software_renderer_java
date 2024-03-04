package renderer;

public class Vector4f
{
    public float[] data = new float[4];

    public Vector4f()
    {
        data[0] = 0;
        data[1] = 0;
        data[2] = 0;
        data[3] = 1;
    }

    public Vector4f(float x, float y, float z)
    {
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = 1;
    }

    public Vector4f(float x, float y, float z, float w)
    {
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = w;
    }
}
