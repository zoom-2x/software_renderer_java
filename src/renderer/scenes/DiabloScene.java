package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.Vector4f;

import renderer.program.ProgramGouraud;

public class DiabloScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = { 0, 0, Mathlib.PI / 10000 };

    public DiabloScene(Renderer renderer)
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

        renderer.set_flag(Renderer.FLAG_FILTERING);

        renderer.camera.eye.data[0] = 0;
        renderer.camera.eye.data[1] = -3;
        renderer.camera.eye.data[2] = 0.5f;

        renderer.camera.target.data[0] = 0;
        renderer.camera.target.data[1] = 0;
        renderer.camera.target.data[2] = 0.1f;

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("diablo");
        renderer.texture = assets.textures.get("diablo");

        ProgramGouraud program = new ProgramGouraud();
        renderer.program = program;

        // program.color = new Vector4f(233, 159, 105);
        program.light_intensity = 2.5f;
        program.specular_intensity = 0.8f;
        program.shininess = 50.0f;
        program.light_dir = Mathlib.vectorn(-0.2f, -1.0f, -1.5f);
        program.ambient_color = Mathlib.color_linear(119, 55, 92);
        program.light_color = Mathlib.color_linear(247, 225, 159);
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
