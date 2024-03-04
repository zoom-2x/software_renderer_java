package renderer.command;

import renderer.Renderer;
import renderer.Assets;

public class RendererCommand
{
    private static RendererCommand instance;
    private Renderer renderer;

    private RendererCommand() {}

    public static void setup(Renderer renderer) {
        RendererCommand.get().renderer = renderer;
    }

    public static RendererCommand get()
    {
        if (instance == null)
            instance = new RendererCommand();

        return instance;
    }

    public void load_scene(int scene_index)
    {
        if (Assets.get_assets().valid_scene(scene_index))
            renderer.scene = Assets.get_assets().load_scene(renderer, scene_index);
    }

    public void pause() {
        renderer.pause = !renderer.pause;
    }

    public void fps(int fps) {
        renderer.set_fps(fps);
    }

    public void filtering() {
        renderer.xor_flag(Renderer.FLAG_FILTERING);
    }

    public void mode() {
        renderer.switch_mode();
    }

    public void postprocessing() {
        renderer.use_postprocessing = !renderer.use_postprocessing;
    }
}