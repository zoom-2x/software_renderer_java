package renderer;

public class Mathlib
{
    public static float PI = 3.14159265358979323846f;
    public static float TWO_PI = 6.28318530717958647693f;
    public static float ONE80_OVER_PI = 57.2957795130823208768f;
    public static float PI_OVER_ONE80 = 0.01745329251994329577f;
    public static float EPSILON = 0.0000001f;
    public static float ONE_OVER_255 = 0.00392156862745098039f;

    public static float rad2deg(float angle) { return angle * Mathlib.ONE80_OVER_PI; }
    public static float deg2rad(float angle) { return angle * Mathlib.PI_OVER_ONE80; }

    public static void color_one_to_255(Vector4f c)
    {
        c.data[0] *= 255;
        c.data[1] *= 255;
        c.data[2] *= 255;
    }

    public static void color_255_to_one(Vector4f c)
    {
        c.data[0] *= Mathlib.ONE_OVER_255;
        c.data[1] *= Mathlib.ONE_OVER_255;
        c.data[2] *= Mathlib.ONE_OVER_255;
    }

    public static Vector4f color_linear(float r, float g, float b)
    {
        Vector4f color = new Vector4f(r, g, b);
        Mathlib.color_255_to_one(color);

        return color;
    }

    public static float clamp(float v, float min, float max) {
        return Math.min(Math.max(v, min), max);
    }

    public static void color_clamp(Vector4f c)
    {
        c.data[0] = Mathlib.clamp(c.data[0], 0, 1);
        c.data[1] = Mathlib.clamp(c.data[1], 0, 1);
        c.data[2] = Mathlib.clamp(c.data[2], 0, 1);
    }

    public static void color_reinhard(Vector4f c)
    {
        c.data[0] = 255 * c.data[0] / (c.data[0] + 255);
        c.data[1] = 255 * c.data[1] / (c.data[1] + 255);
        c.data[2] = 255 * c.data[2] / (c.data[2] + 255);
    }

    public static Vector4f vec3_add(Vector4f v1, Vector4f v2)
    {
        Vector4f res = new Vector4f();

        res.data[0] = v1.data[0] + v2.data[0];
        res.data[1] = v1.data[1] + v2.data[1];
        res.data[2] = v1.data[2] + v2.data[2];

        return res;
    }

    public static Vector4f vec3_sub(Vector4f v1, Vector4f v2)
    {
        Vector4f res = new Vector4f();

        res.data[0] = v1.data[0] - v2.data[0];
        res.data[1] = v1.data[1] - v2.data[1];
        res.data[2] = v1.data[2] - v2.data[2];

        return res;
    }

    public static float vec3_dot(Vector4f v1, Vector4f v2) {
        return (v1.data[0] * v2.data[0] + v1.data[1] * v2.data[1] + v1.data[2] * v2.data[2]);
    }

    public static float vec3_len(Vector4f v) {
        return (float) Math.sqrt(Mathlib.vec3_dot(v, v));
    }

    public static float vec3_len2(Vector4f v) {
        return Mathlib.vec3_dot(v, v);
    }

    public static Vector4f vec3_cross(Vector4f v1, Vector4f v2)
    {
        Vector4f res = new Vector4f();

        res.data[0] = v1.data[1] * v2.data[2] - v1.data[2] * v2.data[1];
        res.data[1] = v1.data[2] * v2.data[0] - v1.data[0] * v2.data[2];
        res.data[2] = v1.data[0] * v2.data[1] - v1.data[1] * v2.data[0];

        return res;
    }

    public static void vec3_normalize(Vector4f v)
    {
        float len = 1.0f / Mathlib.vec3_len(v);

        v.data[0] *= len;
        v.data[1] *= len;
        v.data[2] *= len;
    }

    public static Vector4f vectorn(float x, float y, float z)
    {
        Vector4f res = new Vector4f(x, y, z);
        Mathlib.vec3_normalize(res);

        return res;
    }

    public static void translation(Vector4f t, Matrix4f mat)
    {
        mat.identity();

        mat.data[3] = t.data[0];
        mat.data[7] = t.data[1];
        mat.data[11] = t.data[2];
    }

    public static void rotation_x(float angle, Matrix4f mat)
    {
        mat.identity();

        float cos_t = (float) Math.cos(angle);
        float sin_t = (float) Math.sin(angle);

        mat.data[5] = cos_t;
        mat.data[6] = -sin_t;
        mat.data[9] = sin_t;
        mat.data[10] = cos_t;
    }

    public static void rotation_y(float angle, Matrix4f mat)
    {
        mat.identity();

        float cos_t = (float) Math.cos(angle);
        float sin_t = (float) Math.sin(angle);

        mat.data[0] = cos_t;
        mat.data[8] = -sin_t;
        mat.data[2] = sin_t;
        mat.data[10] = cos_t;
    }

    public static void rotation_z(float angle, Matrix4f mat)
    {
        mat.identity();

        float cos_t = (float) Math.cos(angle);
        float sin_t = (float) Math.sin(angle);

        mat.data[0] = cos_t;
        mat.data[4] = sin_t;
        mat.data[1] = -sin_t;
        mat.data[5] = cos_t;
    }

    public static void scale(float sx, float sy, float sz, Matrix4f mat)
    {
        mat.identity();

        mat.data[0] = sx;
        mat.data[5] = sy;
        mat.data[10] = sz;
    }

    public static void scale(Vector4f s, Matrix4f mat)
    {
        mat.identity();

        mat.data[0] = s.data[0];
        mat.data[5] = s.data[1];
        mat.data[10] = s.data[2];
    }

    public static void mat4_mul(Matrix4f m1, Matrix4f m2, Matrix4f dest)
    {
        dest.data[0] = m1.data[0] * m2.data[0] + m1.data[1] * m2.data[4] + m1.data[2] * m2.data[8] + m1.data[3] * m2.data[12];
        dest.data[1] = m1.data[0] * m2.data[1] + m1.data[1] * m2.data[5] + m1.data[2] * m2.data[9] + m1.data[3] * m2.data[13];
        dest.data[2] = m1.data[0] * m2.data[2] + m1.data[1] * m2.data[6] + m1.data[2] * m2.data[10] + m1.data[3] * m2.data[14];
        dest.data[3] = m1.data[0] * m2.data[3] + m1.data[1] * m2.data[7] + m1.data[2] * m2.data[11] + m1.data[3] * m2.data[15];

        dest.data[4] = m1.data[4] * m2.data[0] + m1.data[5] * m2.data[4] + m1.data[6] * m2.data[8] + m1.data[7] * m2.data[12];
        dest.data[5] = m1.data[4] * m2.data[1] + m1.data[5] * m2.data[5] + m1.data[6] * m2.data[9] + m1.data[7] * m2.data[13];
        dest.data[6] = m1.data[4] * m2.data[2] + m1.data[5] * m2.data[6] + m1.data[6] * m2.data[10] + m1.data[7] * m2.data[14];
        dest.data[7] = m1.data[4] * m2.data[3] + m1.data[5] * m2.data[7] + m1.data[6] * m2.data[11] + m1.data[7] * m2.data[15];

        dest.data[8] = m1.data[8] * m2.data[0] + m1.data[9] * m2.data[4] + m1.data[10] * m2.data[8] + m1.data[11] * m2.data[12];
        dest.data[9] = m1.data[8] * m2.data[1] + m1.data[9] * m2.data[5] + m1.data[10] * m2.data[9] + m1.data[11] * m2.data[13];
        dest.data[10] = m1.data[8] * m2.data[2] + m1.data[9] * m2.data[6] + m1.data[10] * m2.data[10] + m1.data[11] * m2.data[14];
        dest.data[11] = m1.data[8] * m2.data[3] + m1.data[9] * m2.data[7] + m1.data[10] * m2.data[11] + m1.data[11] * m2.data[15];

        dest.data[12] = m1.data[12] * m2.data[0] + m1.data[13] * m2.data[4] + m1.data[14] * m2.data[8] + m1.data[15] * m2.data[12];
        dest.data[13] = m1.data[12] * m2.data[1] + m1.data[13] * m2.data[5] + m1.data[14] * m2.data[9] + m1.data[15] * m2.data[13];
        dest.data[14] = m1.data[12] * m2.data[2] + m1.data[13] * m2.data[6] + m1.data[14] * m2.data[10] + m1.data[15] * m2.data[14];
        dest.data[15] = m1.data[12] * m2.data[3] + m1.data[13] * m2.data[7] + m1.data[14] * m2.data[11] + m1.data[15] * m2.data[15];
    }

    public static float mat4_det(Matrix4f m)
    {
        // (0,0) minor.
        float det00 = m.data[5] * (m.data[10] * m.data[15] - m.data[11] * m.data[14]) -
                      m.data[6] * (m.data[9] * m.data[15] - m.data[11] * m.data[13]) +
                      m.data[7] * (m.data[9] * m.data[14] - m.data[10] * m.data[13]);

        // (0,1) minor.
        float det01 = m.data[4] * (m.data[10] * m.data[15] - m.data[11] * m.data[14]) -
                      m.data[6] * (m.data[8] * m.data[15] - m.data[11] * m.data[12]) +
                      m.data[7] * (m.data[8] * m.data[14] - m.data[10] * m.data[12]);

        // (0,2) minor.
        float  det02 = m.data[4] * (m.data[9] * m.data[15] - m.data[11] * m.data[13]) -
                       m.data[5] * (m.data[8] * m.data[15] - m.data[11] * m.data[12]) +
                       m.data[7] * (m.data[8] * m.data[13] - m.data[9] * m.data[12]);

        // (0,3) minor.
        float det03 = m.data[4] * (m.data[9] * m.data[14] - m.data[10] * m.data[13]) -
                      m.data[5] * (m.data[8] * m.data[14] - m.data[10] * m.data[12]) +
                      m.data[6] * (m.data[8] * m.data[13] - m.data[9] * m.data[12]);

        float result = m.data[0] * det00 - m.data[1] * det01 + m.data[2] * det02 - m.data[3] * det03;

        return result;
    }

    public static void mat4_inverse(Matrix4f in, Matrix4f out)
    {
        float one_over_det = 1.0f / mat4_det(in);
        float[] tmp = new float[36];

        tmp[0] = in.data[10] * in.data[15];
        tmp[1] = in.data[11] * in.data[14];
        tmp[2] = in.data[9] * in.data[15];
        tmp[3] = in.data[11] * in.data[13];
        tmp[4] = in.data[9] * in.data[14];
        tmp[5] = in.data[10] * in.data[13];
        tmp[6] = in.data[8] * in.data[15];
        tmp[7] = in.data[11] * in.data[12];
        tmp[8] = in.data[8] * in.data[14];
        tmp[9] = in.data[10] * in.data[12];
        tmp[10] = in.data[8] * in.data[13];
        tmp[11] = in.data[9] * in.data[12];
        tmp[12] = in.data[6] * in.data[15];
        tmp[13] = in.data[7] * in.data[14];
        tmp[14] = in.data[5] * in.data[15];
        tmp[15] = in.data[7] * in.data[13];
        tmp[16] = in.data[5] * in.data[14];
        tmp[17] = in.data[6] * in.data[13];
        tmp[18] = in.data[4] * in.data[15];
        tmp[19] = in.data[7] * in.data[12];
        tmp[20] = in.data[4] * in.data[14];
        tmp[21] = in.data[6] * in.data[12];
        tmp[22] = in.data[4] * in.data[13];
        tmp[23] = in.data[5] * in.data[12];
        tmp[24] = in.data[6] * in.data[11];
        tmp[25] = in.data[7] * in.data[10];
        tmp[26] = in.data[5] * in.data[11];
        tmp[27] = in.data[7] * in.data[9];
        tmp[28] = in.data[5] * in.data[10];
        tmp[29] = in.data[6] * in.data[9];
        tmp[30] = in.data[4] * in.data[11];
        tmp[31] = in.data[7] * in.data[8];
        tmp[32] = in.data[4] * in.data[10];
        tmp[33] = in.data[6] * in.data[8];
        tmp[34] = in.data[4] * in.data[9];
        tmp[35] = in.data[5] * in.data[8];

        float e11 = in.data[5] * (tmp[0] - tmp[1]) - in.data[6] * (tmp[2] - tmp[3]) + in.data[7] * (tmp[4] - tmp[5]);
        float e12 = in.data[4] * (tmp[0] - tmp[1]) - in.data[6] * (tmp[6] - tmp[7]) + in.data[7] * (tmp[8] - tmp[9]);
        float e13 = in.data[4] * (tmp[2] - tmp[3]) - in.data[5] * (tmp[6] - tmp[7]) + in.data[7] * (tmp[10] - tmp[11]);
        float e14 = in.data[4] * (tmp[4] - tmp[5]) - in.data[5] * (tmp[8] - tmp[9]) + in.data[6] * (tmp[10] - tmp[11]);
        float e21 = in.data[1] * (tmp[0] - tmp[1]) - in.data[2] * (tmp[2] - tmp[3]) + in.data[3] * (tmp[4] - tmp[5]);
        float e22 = in.data[0] * (tmp[0] - tmp[1]) - in.data[2] * (tmp[6] - tmp[7]) + in.data[3] * (tmp[8] - tmp[9]);
        float e23 = in.data[0] * (tmp[2] - tmp[3]) - in.data[1] * (tmp[6] - tmp[7]) + in.data[3] * (tmp[10] - tmp[11]);
        float e24 = in.data[0] * (tmp[4] - tmp[5]) - in.data[1] * (tmp[8] - tmp[9]) + in.data[2] * (tmp[10] - tmp[11]);
        float e31 = in.data[1] * (tmp[12] - tmp[13]) - in.data[2] * (tmp[14] - tmp[15]) + in.data[3] * (tmp[16] - tmp[17]);
        float e32 = in.data[0] * (tmp[12] - tmp[13]) - in.data[2] * (tmp[18] - tmp[19]) + in.data[3] * (tmp[20] - tmp[21]);
        float e33 = in.data[0] * (tmp[14] - tmp[15]) - in.data[1] * (tmp[18] - tmp[19]) + in.data[3] * (tmp[22] - tmp[23]);
        float e34 = in.data[0] * (tmp[16] - tmp[17]) - in.data[1] * (tmp[20] - tmp[21]) + in.data[2] * (tmp[22] - tmp[23]);
        float e41 = in.data[1] * (tmp[24] - tmp[25]) - in.data[2] * (tmp[26] - tmp[27]) + in.data[3] * (tmp[28] - tmp[29]);
        float e42 = in.data[0] * (tmp[24] - tmp[25]) - in.data[2] * (tmp[30] - tmp[31]) + in.data[3] * (tmp[32] - tmp[33]);
        float e43 = in.data[0] * (tmp[26] - tmp[27]) - in.data[1] * (tmp[30] - tmp[31]) + in.data[3] * (tmp[34] - tmp[35]);
        float e44 = in.data[0] * (tmp[28] - tmp[29]) - in.data[1] * (tmp[32] - tmp[33]) + in.data[2] * (tmp[34] - tmp[35]);

        out.data[0] = one_over_det * e11;
        out.data[1] = -one_over_det * e21;
        out.data[2] = one_over_det * e31;
        out.data[3] = -one_over_det * e41;

        out.data[4] = -one_over_det * e12;
        out.data[5] = one_over_det * e22;
        out.data[6] = -one_over_det * e32;
        out.data[7] = one_over_det * e42;

        out.data[8] = one_over_det * e13;
        out.data[9] = -one_over_det * e23;
        out.data[10] = one_over_det * e33;
        out.data[11] = -one_over_det * e43;

        out.data[12] = -one_over_det * e14;
        out.data[13] = one_over_det * e24;
        out.data[14] = -one_over_det * e34;
        out.data[15] = one_over_det * e44;
    }

    // inv(transpose(mat3x3))
    public static void mat4_normal(Matrix4f in, Matrix4f out)
    {
        Matrix4f tmp = new Matrix4f();

        tmp.data[0] = in.data[0];
        tmp.data[1] = in.data[4];
        tmp.data[2] = in.data[8];
        tmp.data[3] = 0;

        tmp.data[4] = in.data[1];
        tmp.data[5] = in.data[5];
        tmp.data[6] = in.data[9];
        tmp.data[7] = 0;

        tmp.data[8] = in.data[2];
        tmp.data[9] = in.data[6];
        tmp.data[10] = in.data[10];
        tmp.data[11] = 0;

        tmp.data[12] = 0;
        tmp.data[13] = 0;
        tmp.data[14] = 0;
        tmp.data[15] = 1;

        mat4_inverse(tmp, out);
    }

    public static void mat4_lookat(Vector4f eye, Vector4f target, Vector4f up, Matrix4f dest)
    {
        dest.identity();

        Vector4f z_axis = Mathlib.vec3_sub(eye, target);
        Vector4f x_axis = Mathlib.vec3_cross(up, z_axis);
        Vector4f y_axis = Mathlib.vec3_cross(z_axis, x_axis);

        Mathlib.vec3_normalize(z_axis);
        Mathlib.vec3_normalize(x_axis);
        Mathlib.vec3_normalize(y_axis);

        // orthogonal axis => inverse = transpose (rotation).
        dest.data[0] = x_axis.data[0];
        dest.data[1] = x_axis.data[1];
        dest.data[2] = x_axis.data[2];

        dest.data[4] = y_axis.data[0];
        dest.data[5] = y_axis.data[1];
        dest.data[6] = y_axis.data[2];

        dest.data[8] = z_axis.data[0];
        dest.data[9] = z_axis.data[1];
        dest.data[10] = z_axis.data[2];

        // Extract the eye's vector components relative to the camera axis (translation).
        dest.data[3] = -Mathlib.vec3_dot(x_axis, eye);
        dest.data[7] = -Mathlib.vec3_dot(y_axis, eye);
        dest.data[11] = -Mathlib.vec3_dot(z_axis, eye);
    }

    public static void mat4_perspective(float aspect, float fov, float f_near, float f_far, Matrix4f dest)
    {
        float d = (float) (1.0f / Math.tan(fov * 0.5f));
        float s = f_near - f_far;

        dest.data[0] = d / aspect;
        dest.data[5] = d;
        dest.data[10] = (f_near + f_far) / s;
        dest.data[14] = -1;
        dest.data[11] = 2 * f_near * f_far / s;
    }

    public static void mat4_orthographic(float f_right, float f_left, float f_top, float f_bottom, float f_near, float f_far, Matrix4f dest)
    {
        dest.empty();

        float rml = f_right - f_left;
        float fmn = f_far - f_near;
        float tmb = f_top - f_bottom;

        dest.data[0] = 2 / rml;
        dest.data[5] = 2 / tmb;
        dest.data[10] = -2 / fmn;
        dest.data[15] = 1;
        dest.data[3] = -(f_right + f_left) / rml;
        dest.data[7] = -(f_top + f_bottom) / tmb;
        dest.data[11] = -(f_far + f_near) / fmn;
    }

    public static void mat4_viewport(int ws, int hs, int sx, int sy, float ds, Matrix4f dest)
    {
        dest.empty();

        float wh = ws / 2.0f;
        float hh = hs / 2.0f;
        float dh = ds / 2.0f;

        dest.data[0] = wh;
        dest.data[3] = wh + sx;
        dest.data[5] = -hh;
        dest.data[7] = hh + sy;
        dest.data[10] = dh;
        dest.data[11] = dh;
        dest.data[15] = 1;
    }

    public static Vector4f mulvec(Vector4f v, Matrix4f m)
    {
        Vector4f res = new Vector4f();

        res.data[0] = m.data[0] * v.data[0] + m.data[1] * v.data[1] + m.data[2] * v.data[2] + m.data[3] * v.data[3];
        res.data[1] = m.data[4] * v.data[0] + m.data[5] * v.data[1] + m.data[6] * v.data[2] + m.data[7] * v.data[3];
        res.data[2] = m.data[8] * v.data[0] + m.data[9] * v.data[1] + m.data[10] * v.data[2] + m.data[11] * v.data[3];
        res.data[3] = m.data[12] * v.data[0] + m.data[13] * v.data[1] + m.data[14] * v.data[2] + m.data[15] * v.data[3];

        return res;
    }

    public static void transform_vertex(Vertex v, Matrix4f m)
    {
        float[] tmp = {v.data[0], v.data[1], v.data[2], v.data[3]};

        v.data[0] = m.data[0] * tmp[0] + m.data[1] * tmp[1] + m.data[2] * tmp[2] + m.data[3];
        v.data[1] = m.data[4] * tmp[0] + m.data[5] * tmp[1] + m.data[6] * tmp[2] + m.data[7];
        v.data[2] = m.data[8] * tmp[0] + m.data[9] * tmp[1] + m.data[10] * tmp[2] + m.data[11];
        v.data[3] = m.data[12] * tmp[0] + m.data[13] * tmp[1] + m.data[14] * tmp[2] + m.data[15];
    }

    public static void perspective_division(Vertex v)
    {
        v.data[3] = 1.0f / v.data[3];
        v.data[0] = v.data[0] * v.data[3];
        v.data[1] = v.data[1] * v.data[3];
        v.data[2] = v.data[2] * v.data[3];
    }

    public static void viewport_transform(Vertex v, Matrix4f m)
    {
        v.data[0] = m.data[0] * v.data[0] + m.data[3];
        v.data[1] = m.data[5] * v.data[1] + m.data[7];
        v.data[2] = m.data[10] * v.data[2] + m.data[11];
        v.data[3] = v.data[3];
    }

    public static void triangle_project(Triangle t, Matrix4f m)
    {
        Vertex v;
        float[] tmp = {0, 0, 0, 0};

        for (int i = 0; i < 3; ++i)
        {
            v = t.vertices[i];

            tmp[0] = v.data[0];
            tmp[1] = v.data[1];
            tmp[2] = v.data[2];

            v.data[0] = m.data[0] * tmp[0] + m.data[1] * tmp[1] + m.data[2] * tmp[2] + m.data[3];
            v.data[1] = m.data[4] * tmp[0] + m.data[5] * tmp[1] + m.data[6] * tmp[2] + m.data[7];
            v.data[2] = m.data[8] * tmp[0] + m.data[9] * tmp[1] + m.data[10] * tmp[2] + m.data[11];
            v.data[3] = m.data[12] * tmp[0] + m.data[13] * tmp[1] + m.data[14] * tmp[2] + m.data[15];

            tmp[3] = 1.0f / v.data[3];

            v.data[0] *= tmp[3];
            v.data[1] *= tmp[3];
            v.data[2] *= tmp[3];
            v.data[3] = tmp[3];
        }
    }

    public static void triangle_viewport_transform(Triangle t, Matrix4f m)
    {
        Vertex v;

        for (int i = 0; i < 3; ++i)
        {
            v = t.vertices[i];

            v.data[0] = m.data[0] * v.data[0] + m.data[3];
            v.data[1] = m.data[5] * v.data[1] + m.data[7];
            v.data[2] = m.data[10] * v.data[2] + m.data[11];
        }
    }
}
