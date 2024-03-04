package renderer.command;

public class Command
{
    public static void execute(String[] args)
    {
        if ("clear".equals(args[0])) {
            ConsoleCommand.get().clear();
        }
        else if ("help".equals(args[0])) {
            ConsoleCommand.get().show_help();
        }
        else if ("list".equals(args[0])) {
            ConsoleCommand.get().list();
        }
        else if ("pause".equals(args[0])) {
            RendererCommand.get().pause();
        }
        else if ("load".equals(args[0]))
        {
            if (args.length > 1)
            {
                try
                {
                    int scene_index = Integer.parseInt(args[1]) - 1;
                    RendererCommand.get().load_scene(scene_index);
                }
                catch (NumberFormatException e) {}
            }
            else
                ConsoleCommand.get().push("[load] missing argument");
        }
        else if ("fps".equals(args[0]))
        {
            if (args.length > 1)
            {
                try
                {
                    int fps = Integer.parseInt(args[1]);
                    RendererCommand.get().fps(fps);
                }
                catch (NumberFormatException e) {}
            }
            else
                ConsoleCommand.get().push("[fps] missing argument");
        }
        else if ("mode".equals(args[0])) {
            RendererCommand.get().mode();
        }
        else if ("filtering".equals(args[0])) {
            RendererCommand.get().filtering();
        }
        else if ("postprocessing".equals(args[0])) {
            RendererCommand.get().postprocessing();
        }
        else
            ConsoleCommand.get().push("invalid command");
    }
}
