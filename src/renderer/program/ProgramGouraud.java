package renderer.program;

import renderer.Triangle;
import renderer.Mesh;
import renderer.Vector4f;
import renderer.Sampler;
import renderer.Mathlib;
import renderer.Matrices;

public class ProgramGouraud extends ProgramBase
{
    // uv, dot, spec_dot
    public int attrs = 4;

    public float light_intensity = 1;
    public float specular_intensity = 0.6f;

    public Vector4f light_dir = Mathlib.vectorn(0.3f, -1, 0.5f);
    public Vector4f light_color = Mathlib.color_linear(250, 220, 180);
    public Vector4f ambient_color = Mathlib.color_linear(80, 50, 55);
    public Vector4f forced_color = null;
    public float shininess = 50.0f;

    public int get_attrs() {
        return attrs;
    }

    // pos, uv, normal
    public void reader(Mesh mesh, Triangle triangle, Matrices matrices, Vector4f camera)
    {
        for (int i = 0; i < 3; ++i)
        {
            int vertex_pointer = (mesh.face[mesh.pointer + 0] - 1) * 3;
            int uv_pointer = (mesh.face[mesh.pointer + 1] - 1) * 2;
            int normal_pointer = (mesh.face[mesh.pointer + 2] - 1) * 3;

            // Position.
            triangle.vertices[i].data[0] = mesh.pos[vertex_pointer + 0];
            triangle.vertices[i].data[1] = mesh.pos[vertex_pointer + 1];
            triangle.vertices[i].data[2] = mesh.pos[vertex_pointer + 2];
            triangle.vertices[i].data[3] = 1;

            // UV.
            triangle.vertices[i].data[4] = mesh.uv[uv_pointer + 0];
            triangle.vertices[i].data[5] = mesh.uv[uv_pointer + 1];

            // Normal.
            // triangle.vertices[i].data[6] = mesh.normal[normal_pointer + 0];
            // triangle.vertices[i].data[7] = mesh.normal[normal_pointer + 1];
            // triangle.vertices[i].data[8] = mesh.normal[normal_pointer + 2];

            Vector4f n = new Vector4f(
                            mesh.normal[normal_pointer + 0],
                            mesh.normal[normal_pointer + 1],
                            mesh.normal[normal_pointer + 2]);

            n = Mathlib.mulvec(n, matrices.MAT_NORMAL);
            Mathlib.vec3_normalize(n);

            float dot = Mathlib.clamp(Mathlib.vec3_dot(light_dir, n), 0, 1);
            triangle.vertices[i].data[6] = dot;

            Vector4f pos = new Vector4f(
                triangle.vertices[i].data[0],
                triangle.vertices[i].data[1],
                triangle.vertices[i].data[2],
                1
            );

            Vector4f world_pos = Mathlib.mulvec(pos, matrices.MAT_MODEL);
            Vector4f view_vector = Mathlib.vec3_sub(camera, world_pos);
            Vector4f half_vector = new Vector4f(
                (light_dir.data[0] + view_vector.data[0]) * 0.5f,
                (light_dir.data[1] + view_vector.data[1]) * 0.5f,
                (light_dir.data[2] + view_vector.data[2]) * 0.5f,
                0
            );

            Mathlib.vec3_normalize(half_vector);

            float spec_dot = Mathlib.clamp(Mathlib.vec3_dot(half_vector, n), 0, 1);
            triangle.vertices[i].data[7] = spec_dot;

            mesh.pointer += 3;
        }
    }

    public void shader(float[] interp, Sampler sampler, Vector4f pixel, int x, int y)
    {
        float u = interp[2];
        float v = interp[3];

        if (forced_color != null)
        {
            pixel.data[0] = forced_color.data[0];
            pixel.data[1] = forced_color.data[1];
            pixel.data[2] = forced_color.data[2];
        }
        else
        {
            sampler.sample(u, v, pixel);
            Mathlib.color_255_to_one(pixel);
        }

        float dot = interp[4] * light_intensity;
        float spec_dot = (float) Math.exp(-shininess * (1.0f - interp[5])) * interp[4] * specular_intensity;

        pixel.data[0] = light_color.data[0] * (pixel.data[0] * dot + spec_dot) + pixel.data[0] * ambient_color.data[0];
        pixel.data[1] = light_color.data[1] * (pixel.data[1] * dot + spec_dot) + pixel.data[1] * ambient_color.data[1];
        pixel.data[2] = light_color.data[2] * (pixel.data[2] * dot + spec_dot) + pixel.data[2] * ambient_color.data[2];
    }
}
