package renderer;

import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading
{
    public static final int NUM_THREADS = 4;
    public static final int TRIANGLE_BATCH = 12000;

    public AtomicInteger running_workers = new AtomicInteger(0);
    public boolean main_signal_sent = false;
    public boolean[] worker_signal_sent = new boolean[NUM_THREADS];

    public Worker[] workers = new Worker[NUM_THREADS];
    public Thread[] threads = new Thread[NUM_THREADS];

    public Object main_lock = new Object();
    public Object worker_lock = new Object();

    public int tile_height = 32;
    public int tile_shift = 5;

    public int post_tile_size = 32;
    public int post_tile_shift = 5;

    public AtomicInteger tile_pointer = new AtomicInteger(0);
    public AtomicInteger post_tile_pointer = new AtomicInteger(0);

    public ScanlineTile[] tiles;
    public PostTile[] post_tiles;

    public int batch_count = 0;
    public Triangle[] tribuffer = new Triangle[TRIANGLE_BATCH];

    public Multithreading(ScreenBuffer buffer)
    {
        int tile_count = buffer.height >> tile_shift;

        if (buffer.height % tile_height > 0)
            tile_count++;

        tiles = new ScanlineTile[tile_count];

        for (int i = 0, y = 0; i < tile_count; ++i, y += tile_height) {
            tiles[i] = new ScanlineTile(buffer, y);
        }

        int post_tile_rows = buffer.height >> post_tile_shift;
        int post_tile_cols = buffer.width >> post_tile_shift;

        if (buffer.height % post_tile_size > 0)
            post_tile_rows++;

        if (buffer.width % post_tile_size > 0)
            post_tile_cols++;

        int post_tile_total = post_tile_rows * post_tile_cols;

        int post_tile_index = 0;
        post_tiles = new PostTile[post_tile_total];

        for (int row = 0, y = 0; row < post_tile_rows; ++row, y += post_tile_size)
        {
            for (int col = 0, x = 0; col < post_tile_cols; ++col, x += post_tile_size)
            {
                int ex = x + post_tile_size;
                int ey = y + post_tile_size;

                if (ex >= buffer.width)
                    ex = buffer.width;

                if (ey >= buffer.height)
                    ey = buffer.height;

                post_tiles[post_tile_index++] = new PostTile(x, y, ex, ey);
            }
        }

        for (int i = 0; i < TRIANGLE_BATCH; ++i) {
            tribuffer[i] = new Triangle(8);
        }

        for (int i = 0; i < NUM_THREADS; ++i)
        {
            workers[i] = new Worker(this, buffer, i + 1);
            threads[i] = new Thread(workers[i]);
            threads[i].start();

            worker_signal_sent[i] = false;
        }
    }
}
