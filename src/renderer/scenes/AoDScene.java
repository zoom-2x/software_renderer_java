package renderer.scenes;

import java.util.Random;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.Vector4f;

import renderer.program.ProgramTexture;

public class AoDScene extends Scene
{
    private Random rand = new Random();

    private float time = 0;
    private float scale_scaling = 1;
    private float base_scale = 1;

    private Vector4f base_position = new Vector4f();
    private Vector4f base_rotation = new Vector4f(Mathlib.deg2rad(-10), 0, Mathlib.deg2rad(10));

    public Model model = new Model();

    public AoDScene(Renderer renderer)
    {
        model.position.data[0] = 0;
        model.position.data[1] = 0;
        model.position.data[2] = 0;

        model.rotation.data[0] = 0;
        model.rotation.data[1] = 0;
        model.rotation.data[2] = 0;

        model.scale.data[0] = 1.0f;
        model.scale.data[1] = 1.0f;
        model.scale.data[2] = 1.0f;

        renderer.reset_flags();
        renderer.reset_camera();

        renderer.set_flag(Renderer.FLAG_FILTERING);

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("plane");
        renderer.texture = assets.textures.get("aod");

        ProgramTexture program = new ProgramTexture();
        renderer.program = program;
    }

    public void update(float delta)
    {
        float offset_x = (rand.nextFloat() * 2 - 1) * 0.02f;
        float offset_z = (rand.nextFloat() * 2 - 1) * 0.02f;

        model.position.data[0] = base_position.data[0] + offset_x;
        model.position.data[1] = base_position.data[0];
        model.position.data[2] = base_position.data[2] + offset_z;

        model.rotation.data[0] = base_rotation.data[0] + offset_x * 0.5f * Mathlib.PI;
        // model.rotation.data[1] = base_rotation.data[1] + offset_x * 0.05f * Mathlib.PI;
        model.rotation.data[2] = base_rotation.data[2] + offset_z * 0.5f * Mathlib.PI;

        float c = 0.07f * (float) Math.sin(time * Mathlib.PI / 2000);

        model.scale.data[0] = scale_scaling * base_scale + c;
        model.scale.data[1] = scale_scaling * base_scale;
        model.scale.data[2] = scale_scaling * base_scale + c;

        time += delta;
    }
}