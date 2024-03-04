package renderer.program;

import renderer.Triangle;
import renderer.Mesh;
import renderer.Vector4f;
import renderer.Sampler;
import renderer.Matrices;
import renderer.Mathlib;

public class ProgramTexture extends ProgramBase
{
    // uv
    public int attrs = 2;

    public int get_attrs() {
        return attrs;
    }

    public void reader(Mesh mesh, Triangle triangle, Matrices matrices, Vector4f camera)
    {
        for (int i = 0; i < 3; ++i)
        {
            int vertex_pointer = (mesh.face[mesh.pointer + 0] - 1) * 3;
            int uv_pointer = (mesh.face[mesh.pointer + 1] - 1) * 2;

            // Position.
            triangle.vertices[i].data[0] = mesh.pos[vertex_pointer + 0];
            triangle.vertices[i].data[1] = mesh.pos[vertex_pointer + 1];
            triangle.vertices[i].data[2] = mesh.pos[vertex_pointer + 2];
            triangle.vertices[i].data[3] = 1;

            // UV.
            triangle.vertices[i].data[4] = mesh.uv[uv_pointer + 0];
            triangle.vertices[i].data[5] = mesh.uv[uv_pointer + 1];

            mesh.pointer += 3;
        }
    }

    public void shader(float[] scanline, Sampler sampler, Vector4f color, int x, int y)
    {
        float u = scanline[2];
        float v = scanline[3];

        // float time = (System.currentTimeMillis() & 0xfffff) * 0.007f;

        // float dx = 0.05f * y + time;
        // float dy = 0.03f * x + time;

        // u += 0.012f * Math.sin(dx + dy) * Math.sin(dy);
        // v += 0.008f * Math.cos(dx - dy) * Math.cos(dy);

        // u = Mathlib.clamp(u, 0, 1);
        // v = Mathlib.clamp(v, 0, 1);

        sampler.sample(u, v, color);
        Mathlib.color_255_to_one(color);
    }
}
