package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.Vector4f;

import renderer.program.ProgramCartoon;

public class TeapotCartoonScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = {0, 0, Mathlib.PI / 10000};

    public TeapotCartoonScene(Renderer renderer)
    {
        model.position.data[0] = 0;
        model.position.data[1] = 0;
        model.position.data[2] = 0;

        model.rotation.data[0] = 0;
        model.rotation.data[1] = 0;
        model.rotation.data[2] = 0;

        model.scale.data[0] = 0.5f;
        model.scale.data[1] = 0.5f;
        model.scale.data[2] = 0.5f;

        renderer.reset_flags();
        renderer.reset_camera();

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("teapot");

        ProgramCartoon program = new ProgramCartoon();
        renderer.program = program;

        program.specular_intensity = 1.4f;
        program.shininess = 100;
        program.light_dir = Mathlib.vectorn(1.0f, -1.0f, 0.5f);

        renderer.camera.eye.data[2] = 1;
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
