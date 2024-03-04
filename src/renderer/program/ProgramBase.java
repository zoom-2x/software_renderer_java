package renderer.program;

import renderer.Triangle;
import renderer.Mesh;
import renderer.Vector4f;
import renderer.Sampler;
import renderer.Matrices;

public abstract class ProgramBase
{
    public abstract int get_attrs();
    public abstract void reader(Mesh mesh, Triangle triangle, Matrices matrices, Vector4f camera);
    public abstract void shader(float[] interp, Sampler sampler, Vector4f pixel, int x, int y);
}
