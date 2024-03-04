package renderer;

import java.util.concurrent.ConcurrentLinkedQueue;
import renderer.command.Command;

public class Console
{
    public boolean enabled = true;

    public float fps;
    public float frame_ms;

    private int MAX_FIXED_BUFFERS = 5;
    private int MAX_BUFFERS = 20;
    private int LINE_HEIGHT = 10;

    private byte[] char_color = {(byte) 83, (byte) 157, (byte) 198};

    private int start_pointer = 0;
    private int end_pointer = -1;
    private int count;
    private int fixed_count;

    public boolean input_mode = true;

    private StringBuffer[] fixed_buffer = new StringBuffer[MAX_FIXED_BUFFERS];
    private StringBuffer[] buffer = new StringBuffer[MAX_BUFFERS];
    public StringBuffer last_buffer = new StringBuffer(50);
    public StringBuffer command_buffer = new StringBuffer(50);

    public ConcurrentLinkedQueue<String> command_queue = new ConcurrentLinkedQueue<String>();

    public ConsoleKeyListener key_listener;

    public Console()
    {
        fps = 0;
        frame_ms = 0;

        for (int i = 0; i < MAX_FIXED_BUFFERS; ++i) {
            fixed_buffer[i] = new StringBuffer(50);
        }

        for (int i = 0; i < MAX_BUFFERS; ++i) {
            buffer[i] = new StringBuffer(50);
        }

        key_listener = new ConsoleKeyListener(this);
    }

    public void update() {
        System.out.printf(" > Software renderer / %.2f / %.2f \r", fps, frame_ms);
    }

    public void reset() {
        fixed_count = 0;
    }

    public void clear_buffers()
    {
        start_pointer = 0;
        end_pointer = -1;
        count = 0;
    }

    public void push_fixed(String s)
    {
        if (fixed_count == fixed_buffer.length)
            return;

        StringBuffer buffer = fixed_buffer[fixed_count++];
        buffer.replace(0, 999999, s);
    }

    public void push(String s)
    {
        if (!enabled)
            return;

        end_pointer = (end_pointer + 1) % MAX_BUFFERS;

        StringBuffer buf = buffer[end_pointer];
        buf.replace(0, 999999, s);

        if (count < MAX_BUFFERS)
            count++;
        else
            start_pointer++;

        start_pointer %= MAX_BUFFERS;
    }

    public void draw(Renderer renderer)
    {
        if (!enabled)
            return;

        int sx = 10;
        int sy = 10;

        // Draw the fixed buffers.
        for (int i = 0; i < fixed_count; ++i)
        {
            StringBuffer buf = fixed_buffer[i];
            int x = sx;

            renderer.draw_string_buffer(buf, x, sy, char_color[0], char_color[1], char_color[2]);

            sy += LINE_HEIGHT;
        }

        sy += LINE_HEIGHT;

        // Draw the variable buffers.
        for (int i = start_pointer; i < start_pointer + count; ++i)
        {
            StringBuffer buf = buffer[i % MAX_BUFFERS];
            int x = sx;

            renderer.draw_string_buffer(buf, x, sy, char_color[0], char_color[1], char_color[2]);

            sy += LINE_HEIGHT;
        }

        // Draw the input buffer as well.
        if (input_mode)
            renderer.draw_string_buffer(command_buffer, sx, sy, char_color[0], char_color[1], char_color[2]);
    }

    public void process_commands()
    {
        while (!command_queue.isEmpty())
        {
            String cmd = command_queue.poll();

            if (cmd != null)
            {
                String[] args = cmd.split(" ");
                Command.execute(args);
            }
        }
    }
}
