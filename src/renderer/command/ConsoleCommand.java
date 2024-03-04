package renderer.command;

import renderer.Console;
import renderer.Assets;

public class ConsoleCommand
{
    private static ConsoleCommand instance;
    private Console console;

    public ConsoleCommand() {}

    public static ConsoleCommand get()
    {
        if (instance == null)
            instance = new ConsoleCommand();

        return instance;
    }

    public static void setup(Console console) {
        ConsoleCommand.get().console = console;
    }

    public void show_help()
    {
        console.push("clear: clears the screen");
        console.push("list: list the available scenes");
        console.push("pause: pauses the scene animation");
        console.push("load <value>: loads the specified scene");
        console.push("fps <value>: changes the rendering fps");
        console.push("mode: switches rendering modes");
        console.push("filtering: switches the texture filtering");
        console.push("postprocessing: switches the postprocessing fx");
        console.push("help: displays this help");
    }

    public void clear() {
        console.clear_buffers();
    }

    public void list()
    {
        String[] scene_list = Assets.get_assets().scenes;

        for (int i = 0; i < scene_list.length; ++i) {
            console.push(String.format("%d. %s", i + 1, scene_list[i]));
        }
    }

    public void push(String msg) {
        console.push(msg);
    }
}
