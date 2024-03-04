package renderer;

public class Vertex
{
    public int count;
    public float[] data;

    public Vertex(int attr_count)
    {
        count = 4 + attr_count;
        data = new float[count];
    }
}
