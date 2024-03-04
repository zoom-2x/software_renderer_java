package renderer;

public class Sampler
{
    public boolean filtering = false;
    public Texture tex = null;
    public int[] rgb_offsets = {0, 1, 2};

    public Sampler()
    {}

    public void mode(boolean is_texture)
    {
        if (is_texture)
        {
            rgb_offsets[0] = 0;
            rgb_offsets[1] = 1;
            rgb_offsets[2] = 2;
        }
        else
        {
            rgb_offsets[0] = 3;
            rgb_offsets[1] = 2;
            rgb_offsets[2] = 1;
        }
    }

    public void sample(float u, float v, Vector4f buffer)
    {
        if (filtering)
            sample_filter(u, v, buffer);
        else
            sample_nearest(u, v, buffer);
    }

    protected void sample_nearest(float u, float v, Vector4f buffer)
    {
        u += 0.00001;
        v += 0.00001;

        u = Mathlib.clamp(u, 0, 1);
        v = Mathlib.clamp(v, 0, 1);

        int tex_x = (int) (u * (tex.info.width - 1));
        int tex_y = (int) ((1 - v) * (tex.info.height - 1));
        int pointer = (tex_y * tex.info.width + tex_x) << 2;

        buffer.data[0] = tex.data[pointer + rgb_offsets[0]] & 0xff;
        buffer.data[1] = tex.data[pointer + rgb_offsets[1]] & 0xff;
        buffer.data[2] = tex.data[pointer + rgb_offsets[2]] & 0xff;
    }

    protected void sample_filter(float u, float v, Vector4f buffer)
    {
        Vector4f color_0 = new Vector4f();
        Vector4f color_1 = new Vector4f();
        Vector4f color_2 = new Vector4f();
        Vector4f color_3 = new Vector4f();
        Vector4f color_4 = new Vector4f();
        Vector4f color_5 = new Vector4f();

        u += 0.00001;
        v += 0.00001;

        u = Mathlib.clamp(u, 0, 1);
        v = Mathlib.clamp(v, 0, 1);

        float tex_xf = u * (tex.info.width - 1);
        float tex_yf = (1 - v) * (tex.info.height - 1);

        int tex_xstart = (int) tex_xf;
        int tex_ystart = (int) tex_yf;

        int tex_xend = tex_xstart + 1;
        int tex_yend = tex_ystart + 1;

        if (tex_xend >= tex.info.width) tex_xend = tex.info.width - 1;
        if (tex_yend >= tex.info.height) tex_yend = tex.info.height - 1;

        float s = tex_xf - (float) Math.floor(tex_xf);
        float t = tex_yf - (float) Math.floor(tex_yf);

        int c0 = tex_ystart * tex.info.width;
        int c1 = tex_yend * tex.info.width;

        int pointer_sample_0 = (c0 + tex_xstart) << 2;
        int pointer_sample_1 = (c0 + tex_xend) << 2;
        int pointer_sample_2 = (c1 + tex_xstart) << 2;
        int pointer_sample_3 = (c1 + tex_xend) << 2;

        color_0.data[0] = tex.data[pointer_sample_0 + rgb_offsets[0]] & 0xff;
        color_0.data[1] = tex.data[pointer_sample_0 + rgb_offsets[1]] & 0xff;
        color_0.data[2] = tex.data[pointer_sample_0 + rgb_offsets[2]] & 0xff;

        color_1.data[0] = tex.data[pointer_sample_1 + rgb_offsets[0]] & 0xff;
        color_1.data[1] = tex.data[pointer_sample_1 + rgb_offsets[1]] & 0xff;
        color_1.data[2] = tex.data[pointer_sample_1 + rgb_offsets[2]] & 0xff;

        color_2.data[0] = tex.data[pointer_sample_2 + rgb_offsets[0]] & 0xff;
        color_2.data[1] = tex.data[pointer_sample_2 + rgb_offsets[1]] & 0xff;
        color_2.data[2] = tex.data[pointer_sample_2 + rgb_offsets[2]] & 0xff;

        color_3.data[0] = tex.data[pointer_sample_3 + rgb_offsets[0]] & 0xff;
        color_3.data[1] = tex.data[pointer_sample_3 + rgb_offsets[1]] & 0xff;
        color_3.data[2] = tex.data[pointer_sample_3 + rgb_offsets[2]] & 0xff;

        float rs = 1 - s;
        float rt = 1 - t;

        color_4.data[0] = color_0.data[0] * rs + color_1.data[0] * s;
        color_4.data[1] = color_0.data[1] * rs + color_1.data[1] * s;
        color_4.data[2] = color_0.data[2] * rs + color_1.data[2] * s;

        color_5.data[0] = color_2.data[0] * rs + color_3.data[0] * s;
        color_5.data[1] = color_2.data[1] * rs + color_3.data[1] * s;
        color_5.data[2] = color_2.data[2] * rs + color_3.data[2] * s;

        buffer.data[0] = color_4.data[0] * rt + color_5.data[0] * t;
        buffer.data[1] = color_4.data[1] * rt + color_5.data[1] * t;
        buffer.data[2] = color_4.data[2] * rt + color_5.data[2] * t;
    }
}
