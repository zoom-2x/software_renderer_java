package renderer;

import java.util.Hashtable;
import renderer.pack.PackageLoader;
import renderer.scenes.*;

public class Assets
{
    protected static Assets instance = null;

    public static String[] scenes = {
        "triangle",
        "plane_color",
        "plane_texture",
        "cube_texture",
        "cube_gouraud",
        "torus_knot",
        "blob",
        "blob_cartoon",
        "teapot",
        "teapot_cartoon",
        "monkey",
        "african_head",
        "diablo",
        "headscan",
        "headscan_cartoon",
        "alien",
        "cyberpunk_car",
        "aod"
    };

    public Hashtable<String, Texture> textures = new Hashtable<String, Texture>(20);
    public Hashtable<String, Mesh> meshes = new Hashtable<String, Mesh>(20);

    // public Texture[] textures;
    // public Mesh[] meshes;

    public static Assets get_assets()
    {
        if (Assets.instance == null)
        {
            Assets.instance = new Assets();

            // TODO(gabic): Sa le incarc exterior.
            PackageLoader.load_textures(Assets.instance, "assets/textures.pak");
            PackageLoader.load_meshes(Assets.instance, "assets/meshes.pak");
        }

        return Assets.instance;
    }

    public boolean valid_scene(int scene_index) {
        return (scene_index >= 0 && scene_index < scenes.length);
    }

    public Scene load_scene(Renderer renderer, int scene_index)
    {
        Scene scene = null;

        if ("triangle".equals(scenes[scene_index]))
            scene = new TriangleScene(renderer);
        else if ("plane_color".equals(scenes[scene_index]))
            scene = new PlaneColorScene(renderer);
        else if ("plane_texture".equals(scenes[scene_index]))
            scene = new PlaneTextureScene(renderer);
        else if ("cube_texture".equals(scenes[scene_index]))
            scene = new CubeTextureScene(renderer);
        else if ("cube_gouraud".equals(scenes[scene_index]))
            scene = new CubeGouraudScene(renderer);
        else if ("torus_knot".equals(scenes[scene_index]))
            scene = new TorusKnotScene(renderer);
        else if ("blob".equals(scenes[scene_index]))
            scene = new BlobScene(renderer);
        else if ("blob_cartoon".equals(scenes[scene_index]))
            scene = new BlobCartoonScene(renderer);
        else if ("teapot".equals(scenes[scene_index]))
            scene = new TeapotScene(renderer);
        else if ("teapot_cartoon".equals(scenes[scene_index]))
            scene = new TeapotCartoonScene(renderer);
        else if ("monkey".equals(scenes[scene_index]))
            scene = new MonkeyScene(renderer);
        else if ("african_head".equals(scenes[scene_index]))
            scene = new AfricanHeadScene(renderer);
        else if ("diablo".equals(scenes[scene_index]))
            scene = new DiabloScene(renderer);
        else if ("headscan".equals(scenes[scene_index]))
            scene = new HeadscanScene(renderer);
        else if ("headscan_cartoon".equals(scenes[scene_index]))
            scene = new HeadscanCartoonScene(renderer);
        else if ("alien".equals(scenes[scene_index]))
            scene = new AlienScene(renderer);
        else if ("cyberpunk_car".equals(scenes[scene_index]))
            scene = new CyberpunkCarScene(renderer);
        else if ("aod".equals(scenes[scene_index]))
            scene = new AoDScene(renderer);

        return scene;
    }
}
