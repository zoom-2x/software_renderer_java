package renderer.scenes;

import renderer.Renderer;
import renderer.Model;
import renderer.Mathlib;
import renderer.Assets;

import renderer.program.ProgramTexture;

public class PlaneTextureScene extends Scene
{
    public Model model = new Model();
    protected float animation_speed = Mathlib.PI / 5000;

    public PlaneTextureScene(Renderer renderer)
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
        renderer.unset_flag(Renderer.FLAG_BACKFACE_CULL);

        renderer.camera.eye.data[0] = 0;
        renderer.camera.eye.data[1] = -4;
        renderer.camera.eye.data[2] = 0;

        Assets assets = Assets.get_assets();

        renderer.model = model;
        renderer.mesh = assets.meshes.get("plane");
        renderer.texture = assets.textures.get("ronin");
        renderer.program = new ProgramTexture();
    }

    public void update(float delta)
    {
        model.rotation.data[2] += delta * animation_speed;
    }
}
