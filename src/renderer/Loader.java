package renderer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import renderer.scenes.*;

public class Loader
{
    private static String separator = "----------------------------------------";

    public static void load(Renderer renderer)
    {
        // try
        // {
        //     BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        //     while (renderer.scene == null)
        //     {
        //         System.out.println(separator);
        //         System.out.println(" > Load scene: ");
        //         System.out.println(separator);

        //         for (int i = 0; i < scenes.length; ++i) {
        //             System.out.printf("%d. %s\n", i + 1, scenes[i]);
        //         }

        //         int scene_index = -1;

        //         try {
        //             scene_index = Integer.parseInt(in.readLine()) - 1;
        //         }
        //         catch (NumberFormatException e) {}

        //         if (scene_index >= 0 && scene_index < scenes.length)
        //         {
        //             if ("triangle".equals(scenes[scene_index]))
        //                 renderer.scene = new TriangleScene(renderer);
        //             else if ("plane_color".equals(scenes[scene_index]))
        //                 renderer.scene = new PlaneColorScene(renderer);
        //             else if ("plane_texture".equals(scenes[scene_index]))
        //                 renderer.scene = new PlaneTextureScene(renderer);
        //             else if ("cube_texture".equals(scenes[scene_index]))
        //                 renderer.scene = new CubeTextureScene(renderer);
        //             else if ("cube_gouraud".equals(scenes[scene_index]))
        //                 renderer.scene = new CubeGouraudScene(renderer);
        //             else if ("torus_knot".equals(scenes[scene_index]))
        //                 renderer.scene = new TorusKnotScene(renderer);
        //             else if ("monkey".equals(scenes[scene_index]))
        //                 renderer.scene = new MonkeyScene(renderer);
        //             else if ("african_head".equals(scenes[scene_index]))
        //                 renderer.scene = new AfricanHeadScene(renderer);
        //             else if ("diablo".equals(scenes[scene_index]))
        //                 renderer.scene = new DiabloScene(renderer);
        //             else if ("headscan".equals(scenes[scene_index]))
        //                 renderer.scene = new HeadscanScene(renderer);
        //             else if ("alien".equals(scenes[scene_index]))
        //                 renderer.scene = new AlienScene(renderer);
        //             else if ("cyberpunk_car".equals(scenes[scene_index]))
        //                 renderer.scene = new CyberpunkCarScene(renderer);
        //             else if ("aod".equals(scenes[scene_index]))
        //                 renderer.scene = new AoDScene(renderer);
        //         }
        //         else
        //             System.out.println(" > Invalid scene");
        //     }
        // }
        // catch (IOException e) {}
    }
}
