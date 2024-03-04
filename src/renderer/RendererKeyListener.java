package renderer;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class RendererKeyListener implements KeyListener
{
    private Renderer renderer;

    private int mode_mask = Renderer.FLAG_TRIANGLES | Renderer.FLAG_LINES | Renderer.FLAG_POINTS;
    private int current_mode = 0;

    private int[] mode_list = {
        Renderer.FLAG_TRIANGLES,
        Renderer.FLAG_LINES | Renderer.FLAG_POINTS,
        Renderer.FLAG_LINES,
        Renderer.FLAG_POINTS
    };

    public RendererKeyListener(Renderer renderer) {
        this.renderer = renderer;
    }

    public void keyPressed(KeyEvent e)
    {
        Keyboard keyboard = Keyboard.get_instance();

        if (keyboard.keys[KeyEvent.VK_F1] == true)
        {
            current_mode = (current_mode + 1) % mode_list.length;
            renderer.unset_flag(renderer.flags & mode_mask);
            renderer.set_flag(mode_list[current_mode]);

            keyboard.keys[KeyEvent.VK_F1] = false;
        }

        if (keyboard.keys[KeyEvent.VK_F2] == true)
        {
            renderer.xor_flag(Renderer.FLAG_FILTERING);
            keyboard.keys[KeyEvent.VK_F2] = false;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
