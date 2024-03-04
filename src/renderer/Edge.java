package renderer;

public class Edge
{
    public int count;

    public Vertex v0;
    public Vertex v1;

    public int startyi = 0;
    public int endyi = 0;

    public float x = 0;
    public float dxdy = 0;

    public float[] interp;
    public float[] steps;

    // Format: z, w, attrs ...
    public Edge(int attr_count)
    {
        count = 2 + attr_count;

        interp = new float[count];
        steps = new float[count];
    }

    public void setup(Vertex v0, Vertex v1, Gradients gradients)
    {
        this.v0 = v0;
        this.v1 = v1;

        startyi = (int) Math.ceil(this.v0.data[1]);
        endyi = (int) Math.ceil(this.v1.data[1]);

        float yoffset = startyi - this.v0.data[1];

        this.dxdy = (this.v1.data[0] - this.v0.data[0]) / (this.v1.data[1] - this.v0.data[1]);
        this.x = this.v0.data[0] + dxdy * yoffset;

        float xoffset = this.x - this.v0.data[0];

        // Format: [z, w, attr...]
        // Vertex format: [x, y, z, w, attr...]
        for (int i = 0, k = 2; i < count; ++i, ++k)
        {
            interp[i] = this.v0.data[k] + gradients.dy[i] * yoffset + gradients.dx[i] * xoffset;
            steps[i] = gradients.dy[i] + gradients.dx[i] * this.dxdy;
        }
    }

    public void step()
    {
        this.x += this.dxdy;

        for (int i = 0; i < count; ++i) {
            interp[i] += steps[i];
        }
    }
}
