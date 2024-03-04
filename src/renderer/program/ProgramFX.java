package renderer.program;

import renderer.Vector4f;
import renderer.Mathlib;
import renderer.Sampler;

public class ProgramFX
{
    public static void waves(Sampler sampler, float u, float v, Vector4f pixel)
    {
        float time = (System.currentTimeMillis() & 0xfffff) * 0.005f;

        float du = 1.8f * Mathlib.TWO_PI * u + time;
        float dv = 2.2f * Mathlib.TWO_PI * v + time;

        float c0 = (float) Math.sin(du + dv);
        float c1 = (float) Math.cos(du - dv);
        float c2 = (float) Math.sin(du);
        float c3 = (float) Math.cos(dv);

        u += 0.018f * c0 * c2;
        v += 0.015f * c1 * c3;

        u = Mathlib.clamp(u, 0, 1);
        v = Mathlib.clamp(v, 0, 1);

        sampler.sample(u, v, pixel);
        Mathlib.color_255_to_one(pixel);
    }
}
