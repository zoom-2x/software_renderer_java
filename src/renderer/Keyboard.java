package renderer;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Keyboard implements KeyListener
{
    private static Keyboard instance = null;

    private float ns2ms = 1.0f / 1000000;

    private int DELAY_MS = 16;
    private long prev_timestamp = 0;
    private float delta_ms = 0;

    public boolean[] keys = new boolean[65536];

    public boolean buffer_keys = false;
    public StringBuffer input_buffer = new StringBuffer(50);

    public static Keyboard get_instance()
    {
        if (instance == null)
            instance = new Keyboard();

        return instance;
    }

    public void keyPressed(KeyEvent e)
    {
        long timestamp = System.nanoTime();
        long delta = timestamp - prev_timestamp;
        delta_ms += delta * ns2ms;
        prev_timestamp = timestamp;

        if (delta_ms < DELAY_MS)
            return;

        delta_ms = 0;
        int code = e.getKeyCode();

        if (code >= 0 && code < keys.length)
            keys[code] = true;

        if (buffer_keys)
            input_buffer.append(e.getKeyChar());
    }

    public void keyReleased(KeyEvent e)
    {
        int code = e.getKeyCode();

        if (code >= 0 && code < keys.length)
            keys[code] = false;
    }

    public void keyTyped(KeyEvent e)
    {}
}
