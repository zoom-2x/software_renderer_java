package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.Vector4f;

import renderer.program.ProgramGouraud;

public class TorusKnotScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = {Mathlib.PI / 3000, Mathlib.PI / 10000, 0};

    public TorusKnotScene(Renderer renderer)
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

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("torus_knot");

        ProgramGouraud program = new ProgramGouraud();
        renderer.program = program;

        program.light_intensity = 1;
        program.specular_intensity = 1;
        program.shininess = 50;
        program.light_dir = Mathlib.vectorn(1.0f, -1.0f, 0.5f);
        program.forced_color = Mathlib.color_linear(45, 146, 206);
        program.ambient_color = Mathlib.color_linear(60, 50, 45);
        program.light_color = Mathlib.color_linear(255, 200, 210);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
