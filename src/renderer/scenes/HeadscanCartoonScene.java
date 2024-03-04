package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;

import renderer.program.ProgramCartoon;

public class HeadscanCartoonScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = { 0, 0, Mathlib.PI / 10000 };

    public HeadscanCartoonScene(Renderer renderer)
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
        renderer.mesh = assets.meshes.get("headscan");

        ProgramCartoon program = new ProgramCartoon();
        renderer.program = program;

        program.specular_intensity = 1.4f;
        program.shininess = 100;
        program.light_dir = Mathlib.vectorn(0.5f, -1.5f, 0.5f);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
