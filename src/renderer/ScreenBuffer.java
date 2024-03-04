package renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ScreenBuffer
{
    public int width;
    public int height;
    public int stride;
    public int total;

    public float du;
    public float dv;

    public int channels;
    private int buffer_index;

    public BufferedImage image_buffer;
    public BufferedImage post_buffer;
    public BufferedImage current_buffer;

    public Texture rendered_texture;

    public float[] depth;
    public byte[] data;
    public byte[] post_data;
    public byte[] current_data;

    public ScreenBuffer(int w, int h)
    {
        width = w;
        height = h;
        channels = 4;

        stride = width << 2;
        total = (width * height) << 2;

        du = 1.0f / width;
        dv = 1.0f / height;

        buffer_index = 0;

        image_buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        post_buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        current_buffer = image_buffer;

        data = ((DataBufferByte) (image_buffer.getRaster().getDataBuffer())).getData();
        post_data = ((DataBufferByte) (post_buffer.getRaster().getDataBuffer())).getData();
        current_data = data;

        depth = new float[width * height];

        rendered_texture = new Texture(data, width, height);
    }

    public void switch_buffer(int index)
    {
        if (index == 0)
        {
            this.current_buffer = image_buffer;
            this.current_data = data;
        }
        else if (index == 1)
        {
            this.current_buffer = post_buffer;
            this.current_data = post_data;
        }
    }

    public void clear_screen(byte r, byte g, byte b)
    {
        for (int i = 0; i < current_data.length; i += channels) {
            set_pixel(i, r, g, b);
        }
    }

    public void clear_depth()
    {
        for (int i = 0; i < depth.length; ++i) {
            depth[i] = 1;
        }
    }

    public void set_pixel(int offset, byte r, byte g, byte b)
    {
        current_data[offset + 0] = (byte) 0xff;
        current_data[offset + 1] = b;
        current_data[offset + 2] = g;
        current_data[offset + 3] = r;
    }
}
