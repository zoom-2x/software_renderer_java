package renderer;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class ConsoleKeyListener implements KeyListener
{
    private Console console;

    public ConsoleKeyListener(Console console) {
        this.console = console;
    }

    public void keyPressed(KeyEvent e)
    {
        Keyboard keyboard = Keyboard.get_instance();

        if (keyboard.keys[KeyEvent.VK_F12] == true)
            console.enabled = !console.enabled;

        char c = e.getKeyChar();

        if (!keyboard.keys[KeyEvent.VK_ENTER])
        {
            if (keyboard.keys[KeyEvent.VK_UP]) {
                console.command_buffer.replace(0, 999999, console.last_buffer.toString());
            }
            else if (keyboard.keys[KeyEvent.VK_BACK_SPACE])
            {
                int len = console.command_buffer.length();

                if (len > 0)
                    console.command_buffer.deleteCharAt(len - 1);
            }
            else if (ZXSpectrumFont.get_code(c) >= 0)
                console.command_buffer.append(c);
        }
        else
        {
            console.last_buffer.replace(0, 999999, console.command_buffer.toString());

            console.command_queue.add(console.command_buffer.toString());
            console.push(console.command_buffer.toString());
            console.command_buffer.delete(0, 99999);
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
