package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;

import renderer.program.ProgramGouraud;

public class HeadscanScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = { 0, 0, Mathlib.PI / 10000 };

    public HeadscanScene(Renderer renderer)
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

        renderer.camera.eye.data[0] = 0;
        renderer.camera.eye.data[1] = -3;
        renderer.camera.eye.data[2] = 0;

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("headscan");
        renderer.texture = assets.textures.get("headscan");

        ProgramGouraud program = new ProgramGouraud();
        renderer.program = program;

        // program.color = new Vector4f(233, 159, 105);
        program.light_intensity = 1;
        program.specular_intensity = 0.7f;
        program.shininess = 100;
        program.light_dir = Mathlib.vectorn(1.0f, -0.5f, 2.5f);
        program.ambient_color = Mathlib.color_linear(60, 50, 45);
        program.light_color = Mathlib.color_linear(253, 204, 211);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
