package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Assets;
import renderer.Mathlib;
import renderer.program.ProgramColor;

public class PlaneColorScene extends Scene
{
    public Model model = new Model();
    protected float animation_speed = Mathlib.PI / 5000;

    public PlaneColorScene(Renderer renderer)
    {
        model.base_scale = 0.8f;

        model.position.data[0] = 0;
        model.position.data[1] = 0;
        model.position.data[2] = 0;

        model.rotation.data[0] = 0;
        model.rotation.data[1] = 0;
        model.rotation.data[2] = 0;

        model.scale.data[0] = 0.8f;
        model.scale.data[1] = 0.8f;
        model.scale.data[2] = 0.8f;

        renderer.reset_flags();
        renderer.reset_camera();
        renderer.unset_flag(Renderer.FLAG_BACKFACE_CULL);

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("plane_color");
        renderer.texture = null;
        renderer.program = new ProgramColor();
    }

    public void update(float delta)
    {
        model.rotation.data[2] += delta * animation_speed;
    }
}
