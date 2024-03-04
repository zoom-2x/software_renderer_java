package renderer;

import renderer.struct.TextureInfoStruct;

public class Texture
{
    public TextureInfoStruct info = null;
    public byte[] data;

    public Texture(TextureInfoStruct info)
    {
        this.info = info;
        this.data = new byte[info.bytes];
    }

    // Used to wrap a manual buffer (postprocessing) as a texture.
    public Texture(byte[] data, int width, int height)
    {
        this.data = data;

        this.info = new TextureInfoStruct();

        this.info.width = width;
        this.info.height = height;
        this.info.bytes = width * height * 4;
        this.info.bytes = 0;
    }
}
