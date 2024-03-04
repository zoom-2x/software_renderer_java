package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;

import renderer.program.ProgramGouraud;

public class CubeGouraudScene extends Scene
{
    public Model model = new Model();
    protected float[] animation_speed = {Mathlib.PI / 5000, Mathlib.PI / 7000, Mathlib.PI / 20000};

    public CubeGouraudScene(Renderer renderer)
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
        renderer.camera.eye.data[1] = -5;
        renderer.camera.eye.data[2] = 0;

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("cube");
        renderer.texture = assets.textures.get("ronin");
        renderer.program = new ProgramGouraud();
    }

    public void update(float delta)
    {
        model.rotation.data[0] += delta * animation_speed[0];
        model.rotation.data[1] += delta * animation_speed[1];
        model.rotation.data[2] += delta * animation_speed[2];
    }
}
