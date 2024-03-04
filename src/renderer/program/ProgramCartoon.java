package renderer.program;

import renderer.Triangle;
import renderer.Mesh;
import renderer.Vector4f;
import renderer.Mathlib;
import renderer.Matrices;
import renderer.Sampler;

public class ProgramCartoon extends ProgramBase
{
    // dot, spec_dot
    public int attrs = 2;

    public Vector4f light_dir = Mathlib.vectorn(0.3f, -1, 0.5f);
    public float specular_intensity = 0.6f;
    public float shininess = 50.0f;

    public Vector4f[] color_ramp = {
        Mathlib.color_linear(253, 201, 201),
        Mathlib.color_linear(246, 162, 168),
        Mathlib.color_linear(226, 114, 133),
        Mathlib.color_linear(178, 82, 102),
        Mathlib.color_linear(100, 54, 75)
    };

    public int get_attrs() {
        return attrs;
    }

    // pos, uv, normal
    public void reader(Mesh mesh, Triangle triangle, Matrices matrices, Vector4f camera)
    {
        for (int i = 0; i < 3; ++i)
        {
            int vertex_pointer = (mesh.face[mesh.pointer + 0] - 1) * 3;
            int normal_pointer = (mesh.face[mesh.pointer + 2] - 1) * 3;

            // Position.
            Vector4f pos = new Vector4f(
                mesh.pos[vertex_pointer + 0],
                mesh.pos[vertex_pointer + 1],
                mesh.pos[vertex_pointer + 2],
                1
            );

            // Position.
            triangle.vertices[i].data[0] = pos.data[0];
            triangle.vertices[i].data[1] = pos.data[1];
            triangle.vertices[i].data[2] = pos.data[2];
            triangle.vertices[i].data[3] = 1;

            Vector4f n = new Vector4f(
                            mesh.normal[normal_pointer + 0],
                            mesh.normal[normal_pointer + 1],
                            mesh.normal[normal_pointer + 2]);

            n = Mathlib.mulvec(n, matrices.MAT_NORMAL);
            Mathlib.vec3_normalize(n);

            float dot = Mathlib.clamp(Mathlib.vec3_dot(light_dir, n), 0, 1);
            triangle.vertices[i].data[4] = dot;

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
            triangle.vertices[i].data[5] = spec_dot;

            mesh.pointer += 3;
        }
    }

    public void shader(float[] interp, Sampler sampler, Vector4f pixel, int x, int y)
    {
        float dot = interp[2];
        float spec_dot = (float) Math.exp(-shininess * (1.0f - interp[3])) * interp[2] * specular_intensity;

        if (dot >= 0.95f)
        {
            pixel.data[0] = color_ramp[0].data[0];
            pixel.data[1] = color_ramp[0].data[1];
            pixel.data[2] = color_ramp[0].data[2];
        }
        else if (dot >= 0.75f)
        {
            pixel.data[0] = color_ramp[1].data[0];
            pixel.data[1] = color_ramp[1].data[1];
            pixel.data[2] = color_ramp[1].data[2];
        }
        else if (dot >= 0.5f)
        {
            pixel.data[0] = color_ramp[2].data[0];
            pixel.data[1] = color_ramp[2].data[1];
            pixel.data[2] = color_ramp[2].data[2];
        }
        else if (dot >= 0.2f)
        {
            pixel.data[0] = color_ramp[3].data[0];
            pixel.data[1] = color_ramp[3].data[1];
            pixel.data[2] = color_ramp[3].data[2];
        }
        else if (dot >= 0)
        {
            pixel.data[0] = color_ramp[4].data[0];
            pixel.data[1] = color_ramp[4].data[1];
            pixel.data[2] = color_ramp[4].data[2];
        }

        pixel.data[0] = pixel.data[0] * dot + spec_dot + 0.12f;
        pixel.data[1] = pixel.data[1] * dot + spec_dot + 0.1f;
        pixel.data[2] = pixel.data[2] * dot + spec_dot + 0.1f;
    }
}
