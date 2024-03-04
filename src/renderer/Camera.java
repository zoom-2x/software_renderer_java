package renderer;

public class Camera
{
    public Vector4f eye = new Vector4f();
    public Vector4f target = new Vector4f();
    public Vector4f up = new Vector4f(0, 0, 1);

    public Camera()
    {
        eye.data[0] = 0;
        eye.data[1] = -3;
        eye.data[2] = 0;

        target.data[0] = 0;
        target.data[1] = 0;
        target.data[2] = 0;
    }

    public Camera(float eyex, float eyey, float eyez, float targetx, float targety, float targetz)
    {
        eye.data[0] = eyex;
        eye.data[1] = eyey;
        eye.data[2] = eyez;

        target.data[0] = targetx;
        target.data[1] = targety;
        target.data[2] = targetz;
    }
}
