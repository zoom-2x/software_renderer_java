package renderer.program;

import renderer.Triangle;
import renderer.Mesh;
import renderer.Vector4f;
import renderer.Sampler;
import renderer.Matrices;
import renderer.Mathlib;

public class ProgramColor extends ProgramBase
{
    // color (r, g, b)
    public int attrs = 3;

    public int get_attrs() {
        return attrs;
    }

    public void reader(Mesh mesh, Triangle triangle, Matrices matrices, Vector4f camera)
    {
        for (int i = 0; i < 3; ++i)
        {
            int vertex_pointer = (mesh.face[mesh.pointer + 0] - 1) * 3;
            int color_pointer = (mesh.face[mesh.pointer + 1] - 1) * 3;

            // Position.
            triangle.vertices[i].data[0] = mesh.pos[vertex_pointer + 0];
            triangle.vertices[i].data[1] = mesh.pos[vertex_pointer + 1];
            triangle.vertices[i].data[2] = mesh.pos[vertex_pointer + 2];
            triangle.vertices[i].data[3] = 1;

            // Color.
            Vector4f color = Mathlib.color_linear(
                mesh.color[color_pointer + 0] & 0xff,
                mesh.color[color_pointer + 1] & 0xff,
                mesh.color[color_pointer + 2] & 0xff
            );

            triangle.vertices[i].data[4] = color.data[0];
            triangle.vertices[i].data[5] = color.data[1];
            triangle.vertices[i].data[6] = color.data[2];

            mesh.pointer += 2;
        }
    }

    public void shader(float[] scanline, Sampler sampler, Vector4f color, int x, int y)
    {
        color.data[0] = scanline[2];
        color.data[1] = scanline[3];
        color.data[2] = scanline[4];
    }
}
