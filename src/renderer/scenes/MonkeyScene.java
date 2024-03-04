package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.Vector4f;

import renderer.program.ProgramGouraud;

public class MonkeyScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = {
        Mathlib.PI / 20000,
        Mathlib.PI / 8000,
        Mathlib.PI / 9000
    };

    public MonkeyScene(Renderer renderer)
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

        renderer.camera.eye.data[0] = 0;
        renderer.camera.eye.data[1] = -4;
        renderer.camera.eye.data[2] = 0;

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("monkey");

        ProgramGouraud program = new ProgramGouraud();
        renderer.program = program;

        program.light_intensity = 1;
        program.specular_intensity = 0.7f;
        program.shininess = 100.0f;
        program.light_dir = Mathlib.vectorn(-1.0f, -1.0f, 0.5f);
        program.forced_color = Mathlib.color_linear(233, 159, 105);
        program.ambient_color = Mathlib.color_linear(60, 50, 45);
        program.light_color = Mathlib.color_linear(255.0f, 200.0f, 210.0f);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
