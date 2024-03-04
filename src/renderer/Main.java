package renderer;

import java.awt.image.BufferedImage;
import renderer.pack.PackageTexture;
import renderer.pack.PackageMesh;
import renderer.command.RendererCommand;
import renderer.command.ConsoleCommand;

public class Main
{
    public static void main(String[] args)
    {
        boolean is_cli = args.length > 0 && "-cli".equals(args[0]);

        // ------------------------------------------------------------------------------
        // -- Cli mode.
        // ------------------------------------------------------------------------------

        if (is_cli)
        {
            for (int i = 0; i < args.length; ++i)
            {
                if ("-pack_texture".equals(args[i]))
                {
                    PackageTexture texture_package = new PackageTexture(args[i + 1]);
                    texture_package.generate();
                }
                else if ("-pack_mesh".equals(args[i]))
                {
                    PackageMesh mesh_package = new PackageMesh(args[i + 1]);
                    mesh_package.generate();
                }
            }

            return;
        }

        // ------------------------------------------------------------------------------
        // -- Normal mode.
        // ------------------------------------------------------------------------------

        boolean running = true;

        int WIDTH = 1024;
        int HEIGHT = 800;

        float frame_fps = 0;
        float delta_ms = 0;

        Window window = new Window(WIDTH, HEIGHT, "Software renderer");
        Console console = new Console();
        Renderer renderer = new Renderer(WIDTH, HEIGHT);

        RendererCommand.setup(renderer);
        ConsoleCommand.setup(console);

        window.add_key_listener(console.key_listener);
        window.add_key_listener(renderer.key_listener);

        float ns2ms = 1.0f / 1000000;
        long prev_timestamp = System.nanoTime();
        long timestamp = 0;

        // Main loop.
        while (running)
        {
            console.reset();

            console.push_fixed(String.format("FPS: %.2f", frame_fps));
            console.push_fixed(String.format("Frame ms: %.2f", delta_ms));

            timestamp = System.nanoTime();
            long delta = timestamp - prev_timestamp;
            delta_ms = delta * ns2ms;
            prev_timestamp = timestamp;

            renderer.buffer.switch_buffer(0);
            renderer.buffer.clear_screen(renderer.background[0], renderer.background[1], renderer.background[2]);
            renderer.buffer.clear_depth();

            if (renderer.scene != null)
            {
                if (!renderer.pause)
                    renderer.scene.update(delta_ms);

                if (renderer.use_multithreading)
                    renderer.draw_model_thread();
                else
                    renderer.draw_model();

                console.push_fixed(String.format("Visible tris: %d", renderer.visible_tris));
                console.push_fixed(String.format("Total tris: %d", renderer.mesh.info.total_tris));
            }

            if (renderer.use_postprocessing)
            {
                renderer.buffer.switch_buffer(1);

                if (renderer.use_multithreading)
                    renderer.postprocessing_thread();
                else
                    renderer.postprocessing();
            }

            console.draw(renderer);

            window.flip_buffers(renderer.buffer.current_buffer, renderer.buffer.width, renderer.buffer.height);
            console.process_commands();

            if (delta_ms < renderer.fps_ms)
            {
                try
                {
                    int sleep_ms = (int) (renderer.fps_ms - delta_ms);
                    delta_ms = renderer.fps_ms;
                    Thread.sleep(sleep_ms);
                } catch (Exception e)
                {}
            }

            frame_fps = 1000 / delta_ms;
        }
    }
}
