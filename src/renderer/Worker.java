package renderer;

import renderer.program.ProgramBase;
import renderer.program.ProgramFX;

public class Worker implements Runnable
{
    public static final int STATE_NONE = 0;
    public static final int STATE_LINE = 1;
    public static final int STATE_POINT = 2;
    public static final int STATE_TRIANGLE = 3;
    public static final int STATE_POSTPROCESSING = 4;

    protected int id;
    protected int current_state = -1;
    protected Vector4f pixel_color = new Vector4f();

    private Multithreading mt;
    private ScreenBuffer buffer;

    public ProgramBase program;
    public Sampler sampler;

    public Worker(Multithreading mt, ScreenBuffer buffer, int id)
    {
        this.id = id;
        this.mt = mt;
        this.buffer = buffer;
    }

    public void init_triangle(ProgramBase program, Sampler sampler)
    {
        this.program = program;
        this.sampler = sampler;
        this.current_state = STATE_TRIANGLE;
    }

    public void run()
    {
        while (true)
        {
            if (current_state == STATE_TRIANGLE)
            {
                while (true)
                {
                    int current_index = mt.tile_pointer.getAndIncrement();

                    if (current_index >= mt.tiles.length)
                        break;

                    ScanlineTile tile = mt.tiles[current_index];
                    // System.out.printf("tile: %d %d\n", id, current_index);

                    while (tile.queue.size() > 0)
                    {
                        ScanlineTaskData data = tile.queue.poll();

                        int depth_pointer = (data.y * buffer.width + data.sx);
                        int pixel_pointer = depth_pointer << 2;

                        for (int x = data.sx; x < data.ex; ++x)
                        {
                            if (x >= 0 && x < buffer.width)
                            {
                                float depth = data.interp_base[0];

                                if (depth < buffer.depth[depth_pointer])
                                {
                                    float one_over_w = 1.0f / data.interp_base[1];

                                    // Attribute interpolation.
                                    for (int i = 2; i < data.count; ++i) {
                                        data.interp[i] = data.interp_base[i] * one_over_w;
                                    }

                                    program.shader(data.interp, sampler, pixel_color, x, data.y);

                                    Mathlib.color_clamp(pixel_color);
                                    Mathlib.color_one_to_255(pixel_color);
                                    // Mathlib.color_reinhard(pixel_color);

                                    buffer.depth[depth_pointer] = depth;
                                    buffer.set_pixel(pixel_pointer,
                                                    (byte) pixel_color.data[0],
                                                    (byte) pixel_color.data[1],
                                                    (byte) pixel_color.data[2]);
                                }
                            }

                            // Advance the attributes.
                            for (int i = 0; i < data.count; ++i) {
                                data.interp_base[i] += data.gradients.dx[i];
                            }

                            pixel_pointer += 4;
                            depth_pointer++;
                        }
                    }
                }
            }
            else if (current_state == STATE_POSTPROCESSING)
            {
                while (true)
                {
                    int tile_index = mt.post_tile_pointer.getAndIncrement();

                    if (tile_index >= mt.post_tiles.length)
                        break;

                    PostTile tile = mt.post_tiles[tile_index];

                    float su = tile.sx * buffer.du;
                    float sv = tile.sy * buffer.dv;
                    int base_offset = (tile.sy * buffer.width + tile.sx) << 2;

                    for (int y = tile.sy; y < tile.ey; ++y)
                    {
                        float u = su;
                        int pixel = base_offset;

                        for (int x = tile.sx; x < tile.ex; ++x)
                        {
                            ProgramFX.waves(sampler, u, 1 - sv, pixel_color);
                            Mathlib.color_one_to_255(pixel_color);

                            buffer.set_pixel(pixel,
                                    (byte) pixel_color.data[0],
                                    (byte) pixel_color.data[1],
                                    (byte) pixel_color.data[2]);

                            u += buffer.du;
                            pixel += 4;
                        }

                        sv += buffer.dv;
                        base_offset += buffer.stride;
                    }
                }
            }

            if (current_state > -1)
            {
                current_state = -1;
                int count = mt.running_workers.decrementAndGet();

                if (count == 0)
                {
                    synchronized (mt.main_lock)
                    {
                        mt.main_signal_sent = true;
                        mt.main_lock.notify();
                    }
                }
            }

            synchronized (mt.worker_lock)
            {
                while (!mt.worker_signal_sent[id - 1])
                {
                    try { mt.worker_lock.wait(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }

                mt.worker_signal_sent[id - 1] = false;
            }
        }
    }
}
