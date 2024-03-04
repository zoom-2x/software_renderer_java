package renderer;

public class MatrixStack
{
    protected Matrix4f[] tmp = new Matrix4f[2];
    public Matrix4f[] data = new Matrix4f[10];
    int count = 0;

    public MatrixStack()
    {
        tmp[0] = new Matrix4f();
        tmp[1] = new Matrix4f();

        count = 0;

        for (int i = 0; i < data.length; ++i) {
            data[i] = new Matrix4f();
        }
    }

    public void reset() {
        count = 0;
    }

    public Matrix4f next()
    {
        if (count + 1 > data.length)
            return null;

        return data[count++];
    }

    public void combine(Matrix4f dest)
    {
        int pointer = 0;

        if (count == 0)
            return;

        tmp[pointer].copy(data[0]);

        if (count > 1)
        {
            for (int i = 1; i < count; ++i)
            {
                int next_pointer = (pointer + 1) % 2;

                Mathlib.mat4_mul(tmp[pointer], data[i], tmp[next_pointer]);
                pointer = next_pointer;
            }
        }

        dest.copy(tmp[pointer]);
    }
}
