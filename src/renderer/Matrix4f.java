package renderer;

public class Matrix4f
{
    public float[] data = new float[16];

    public Matrix4f() {
        identity();
    }

    public Matrix4f(boolean empty) {
        empty();
    }

    public void copy(Matrix4f m)
    {
        data[0] = m.data[0];
        data[1] = m.data[1];
        data[2] = m.data[2];
        data[3] = m.data[3];

        data[4] = m.data[4];
        data[5] = m.data[5];
        data[6] = m.data[6];
        data[7] = m.data[7];

        data[8] = m.data[8];
        data[9] = m.data[9];
        data[10] = m.data[10];
        data[11] = m.data[11];

        data[12] = m.data[12];
        data[13] = m.data[13];
        data[14] = m.data[14];
        data[15] = m.data[15];
    }

    public void empty()
    {
        data[0] = 0;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

        data[4] = 0;
        data[5] = 0;
        data[6] = 0;
        data[7] = 0;

        data[8] = 0;
        data[9] = 0;
        data[10] = 0;
        data[11] = 0;

        data[12] = 0;
        data[13] = 0;
        data[14] = 0;
        data[15] = 0;
    }

    public void identity()
    {
        data[0] = 1;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

        data[4] = 0;
        data[5] = 1;
        data[6] = 0;
        data[7] = 0;

        data[8] = 0;
        data[9] = 0;
        data[10] = 1;
        data[11] = 0;

        data[12] = 0;
        data[13] = 0;
        data[14] = 0;
        data[15] = 1;
    }
}
