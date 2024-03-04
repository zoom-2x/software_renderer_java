package renderer;

public class Gradients
{
    public int count;
    public float[] dx;
    public float[] dy;

    public Gradients(int attr_count)
    {
        count = 2 + attr_count;

        dx = new float[count];
        dy = new float[count];
    }

    public void setup(Triangle triangle)
    {
        float dy01 = triangle.vertices[0].data[1] - triangle.vertices[1].data[1];
        float dy20 = triangle.vertices[2].data[1] - triangle.vertices[0].data[1];
        float dx10 = triangle.vertices[1].data[0] - triangle.vertices[0].data[0];
        float dx02 = triangle.vertices[0].data[0] - triangle.vertices[2].data[0];

        // z, w, attrs ...
        for (int i = 0, k = 2; i < count; ++i, ++k)
        {
            dx[i] = (dy01 * (triangle.vertices[2].data[k] - triangle.vertices[0].data[k]) +
                     dy20 * (triangle.vertices[1].data[k] - triangle.vertices[0].data[k])) * triangle.one_over_area;

            dy[i] = (dx10 * (triangle.vertices[2].data[k] - triangle.vertices[0].data[k]) +
                     dx02 * (triangle.vertices[1].data[k] - triangle.vertices[0].data[k])) * triangle.one_over_area;
        }
    }
}
