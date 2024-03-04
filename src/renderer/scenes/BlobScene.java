package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;

import renderer.program.ProgramGouraud;

public class BlobScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = {Mathlib.PI / 3000, Mathlib.PI / 10000, 0};

    public BlobScene(Renderer renderer)
    {
        model.position.data[0] = 0;
        model.position.data[1] = 0;
        model.position.data[2] = 0;

        model.rotation.data[0] = 0;
        model.rotation.data[1] = 0;
        model.rotation.data[2] = 0;

        model.scale.data[0] = 0.6f;
        model.scale.data[1] = 0.5f;
        model.scale.data[2] = 0.8f;

        renderer.reset_flags();
        renderer.reset_camera();

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("blob");

        ProgramGouraud program = new ProgramGouraud();
        renderer.program = program;

        program.light_intensity = 1;
        program.specular_intensity = 1.4f;
        program.shininess = 100;
        program.light_dir = Mathlib.vectorn(1.0f, -1.0f, 0.5f);
        program.forced_color = Mathlib.color_linear(98, 217, 64);
        program.ambient_color = Mathlib.color_linear(77, 30, 39);
        program.light_color = Mathlib.color_linear(211, 241, 39);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
