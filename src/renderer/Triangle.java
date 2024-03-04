package renderer;

public class Triangle
{
    public int id;
    public Vertex[] vertices = new Vertex[3];

    public Edge bottom_top;
    public Edge bottom_middle;
    public Edge middle_top;

    public Gradients gradients;

    public float area;
    public float one_over_area;
    public boolean reversed = false;

    public Triangle(int attr_count)
    {
        gradients = new Gradients(attr_count);

        vertices[0] = new Vertex(attr_count);
        vertices[1] = new Vertex(attr_count);
        vertices[2] = new Vertex(attr_count);

        bottom_top = new Edge(attr_count);
        bottom_middle = new Edge(attr_count);
        middle_top = new Edge(attr_count);
    }

    public float area(int v0, int v1, int v2)
    {
        area = (vertices[v1].data[0] - vertices[v0].data[0]) * (vertices[v2].data[1] - vertices[v0].data[1]) -
               (vertices[v1].data[1] - vertices[v0].data[1]) * (vertices[v2].data[0] - vertices[v0].data[0]);

        return area;
    }

    protected void scanline_direction(int bottom, int top, int middle)
    {
        // Direction of scanline, left to right or right to left.
        float area_check = area(bottom, top, middle);
        reversed = area_check > 0;
    }

    public void setup()
    {
        int bottom_vertex = 0;
        int middle_vertex = 1;
        int top_vertex = 2;

        // Bubble sort the vertices by y.
        if (vertices[top_vertex].data[1] < vertices[middle_vertex].data[1])
        {
            int tmp = middle_vertex;
            middle_vertex = top_vertex;
            top_vertex = tmp;
        }

        if (vertices[middle_vertex].data[1] < vertices[bottom_vertex].data[1])
        {
            int tmp = bottom_vertex;
            bottom_vertex = middle_vertex;
            middle_vertex = tmp;
        }

        if (vertices[top_vertex].data[1] < vertices[middle_vertex].data[1])
        {
            int tmp = middle_vertex;
            middle_vertex = top_vertex;
            top_vertex = tmp;
        }

        one_over_area = 1.0f / area;
        scanline_direction(bottom_vertex, top_vertex, middle_vertex);
        gradients.setup(this);

        bottom_top.setup(vertices[bottom_vertex], vertices[top_vertex], gradients);
        bottom_middle.setup(vertices[bottom_vertex], vertices[middle_vertex], gradients);
        middle_top.setup(vertices[middle_vertex], vertices[top_vertex], gradients);
    }
}
