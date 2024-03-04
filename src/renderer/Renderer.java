package renderer;

import renderer.program.ProgramBase;
import renderer.program.ProgramFX;
import renderer.scenes.*;

public class Renderer
{
    public static final int ATTR_COUNT = 16;

    public static final int FLAG_BACKFACE_CULL = 1;
    public static final int FLAG_FILTERING = 2;
    public static final int FLAG_POINTS = 4;
    public static final int FLAG_LINES = 8;
    public static final int FLAG_TRIANGLES = 16;

    public MatrixStack matrix_stack = new MatrixStack();
    public Matrices matrices = new Matrices();

    public byte[] background = {0, 0, 0};
    public byte[] wireframe_color = {(byte) 56, (byte) 87, (byte) 122};
    public byte[] point_color = {(byte) 83, (byte) 157, (byte) 198};

    public Sampler sampler = new Sampler();
    public Camera camera = new Camera();

    public Model model;
    public Mesh mesh;
    public Texture texture;
    public ProgramBase program;
    public Scene scene;
    public RendererKeyListener key_listener;
    public Multithreading mt;

    public int fps = 120;
    public float fps_ms = 0;
    public float aspect;
    public float fov = 45;
    public int attrs = 3;
    public int flags = FLAG_BACKFACE_CULL | FLAG_TRIANGLES;

    public int scene_to_load = -1;

    public int width;
    public int height;

    protected ScreenBuffer buffer;
    protected Vector4f pixel_color = new Vector4f();

    protected int[] point_offsets = {-1, -1, 0, -1, 1, -1, -1, 0, 1, 0, -1, 1, 0, 1, 1, 1};
    public int visible_tris = 0;

    public boolean use_multithreading = true;
    public boolean use_postprocessing = false;
    public boolean pause = false;

    public Renderer(int width, int height)
    {
        buffer = new ScreenBuffer(width, height);
        set_fps(120);

        this.width = width;
        this.height = height;
        this.aspect = (float) this.width / this.height;

        Mathlib.mat4_perspective(aspect, Mathlib.deg2rad(fov), 0.1f, 10f, matrices.MAT_PROJECTION);
        Mathlib.mat4_viewport(buffer.width, buffer.height, 0, 0, 1f, matrices.MAT_VIEWPORT);

        key_listener = new RendererKeyListener(this);

        if (use_multithreading)
            mt = new Multithreading(buffer);
    }

    public void set_fps(int fps)
    {
        if (fps == 0 || fps < 0)
            this.fps = 240;
        else
            this.fps = fps;

        this.fps_ms = 1000.0f / this.fps;
    }

    private int current_mode = 0;
    private int mode_mask = FLAG_TRIANGLES | FLAG_LINES | FLAG_POINTS;

    private int[] modes = {
        FLAG_TRIANGLES,
        FLAG_LINES | FLAG_POINTS,
        FLAG_LINES,
        FLAG_POINTS
    };

    public void switch_mode()
    {
        current_mode = (current_mode + 1) % modes.length;
        unset_flag(mode_mask);
        set_flag(modes[current_mode]);
    }

    public void reset_flags() {
        flags = FLAG_BACKFACE_CULL | FLAG_TRIANGLES;
    };

    public boolean has_flag(int flag) {
        return (flags & flag) > 0;
    }

    public void set_flag(int flag) {
        flags |= flag;
    }

    public void unset_flag(int flag) {
        flags &= ~flag;
    }

    public void xor_flag(int flag) {
        flags ^= flag;
    }

    public void reset_camera()
    {
        camera.eye.data[0] = 0;
        camera.eye.data[1] = -3;
        camera.eye.data[2] = 0;

        camera.target.data[0] = 0;
        camera.target.data[1] = 0;
        camera.target.data[2] = 0;
    };

    public void draw_rect(int x1, int y1, int x2, int y2, byte r, byte g, byte b)
    {
        int start_offset = (y1 * buffer.width + x1) << 2;

        for (int y = y1; y < y2; ++y)
        {
            int pixel = start_offset;

            for (int x = x1; x < x2; ++x)
            {
                if (x >= 0 && x < buffer.width && y >= 0 && y < buffer.height)
                    buffer.set_pixel(pixel, r, g, b);

                pixel += buffer.channels;
            }

            start_offset += buffer.stride;
        }
    }

    // NOTE(gabic): It's assumed that x1 < x2 and y1 < y2.
    public void draw_image(int x1, int y1, int x2, int y2, Texture texture)
    {
        sampler.filtering = true;
        sampler.tex = texture;

        int start_offset = (y1 * buffer.width + x1) << 2;
        Vector4f color = new Vector4f();

        float du = 1.0f / (x2 - x1);
        float dv = 1.0f / (y2 - y1);
        float u = 0;
        float v = 0;

        for (int y = y1; y < y2; ++y)
        {
            int pixel = start_offset;
            u = 0;

            for (int x = x1; x < x2; ++x)
            {
                if (x >= 0 && x < buffer.width && y >= 0 && y < buffer.height)
                {
                    sampler.sample(u, 1 - v, color);

                    buffer.set_pixel(pixel,
                            (byte) color.data[0],
                            (byte) color.data[1],
                            (byte) color.data[2]);
                }

                pixel += buffer.channels;
                u += du;
            }

            start_offset += buffer.stride;
            v += dv;
        }
    }

    protected boolean valid_point(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    private byte[] char_masks = {
        (byte) 0b10000000,
        (byte) 0b01000000,
        (byte) 0b00100000,
        (byte) 0b00010000,
        (byte) 0b00001000,
        (byte) 0b00000100,
        (byte) 0b00000010,
        (byte) 0b00000001
    };

    private int CHAR_WIDTH = 8;

    protected void draw_char(char c, int x, int y, byte r, byte g, byte b)
    {
        int code = ZXSpectrumFont.get_code(c);

        if (code >= 0)
        {
            int char_offset = code << ZXSpectrumFont.char_stride_shift;
            // TODO(gabic): sa calculez offset-ul pentru fiecare linie a caracterului pentru tot sirul ?
            int pixel_offset = (y * width + x) << 2;

            for (int i = 0; i < 8; ++i)
            {
                byte check_mask = ZXSpectrumFont.data[char_offset + i];
                int pixel = pixel_offset;

                for (int j = 0; j < 8; ++j)
                {
                    if ((check_mask & char_masks[j]) > 0)
                        buffer.set_pixel(pixel, r, g, b);

                    pixel += 4;
                }

                pixel_offset += buffer.stride;
            }
        }
    }

    public void draw_string_buffer(StringBuffer buf, int x, int y, byte r, byte g, byte b)
    {
        for (int j = 0; j < buf.length(); ++j)
        {
            char c = buf.charAt(j);
            draw_char(c, x, y, r, g, b);

            x += CHAR_WIDTH;
        }
    }

    protected void draw_point(float px, float py)
    {
        int x = (int) Math.ceil(px);
        int y = (int) Math.ceil(py);

        int nx = x;
        int ny = y;

        for (int i = 0; i < point_offsets.length; i += 2)
        {
            nx = x + point_offsets[i + 0];
            ny = y + point_offsets[i + 1];

            if (!valid_point(nx, ny))
                continue;

            int buffer_index = (ny * width + nx) << 2;
            buffer.set_pixel(buffer_index, point_color[0], point_color[1], point_color[2]);
        }
    }

    protected void draw_line(float p1x, float p1y, float p2x, float p2y)
    {
        float dx = Math.abs(p2x - p1x);
        float dy = Math.abs(p2y - p1y);

        if (dx >= dy)
        {
            if (p2x < p1x)
            {
                float tmp = p2x;
                p2x = p1x;
                p1x = tmp;

                tmp = p2y;
                p2y = p1y;
                p1y = tmp;
            }

            float mx = (p2y - p1y) / (p2x - p1x);

            int start_xi = (int) Math.ceil(p1x);
            int end_xi = (int) Math.ceil(p2x);
            float y = p1y;

            for (int xi = start_xi; xi <= end_xi; ++xi)
            {
                int yi = (int) Math.ceil(y);

                if (valid_point(xi, yi))
                {
                    int buffer_index = (yi * width + xi) << 2;
                    buffer.set_pixel(buffer_index, wireframe_color[0], wireframe_color[1], wireframe_color[2]);
                }

                y += mx;
            }
        }
        else
        {
            if (p2y < p1y)
            {
                float tmp = p2y;
                p2y = p1y;
                p1y = tmp;

                tmp = p2x;
                p2x = p1x;
                p1x = tmp;
            }

            float my = (p2x - p1x) / (p2y - p1y);

            int start_yi = (int) Math.ceil(p1y);
            int end_yi = (int) Math.ceil(p2y);
            float x = p1x;

            for (int yi = start_yi; yi <= end_yi; ++yi)
            {
                int xi = (int) Math.ceil(x);

                if (valid_point(xi, yi))
                {
                    int buffer_index = (yi * width + xi) << 2;
                    buffer.set_pixel(buffer_index, wireframe_color[0], wireframe_color[1], wireframe_color[2]);
                }

                x += my;
            }
        }
    }

    protected void update_matrices()
    {
        matrix_stack.reset();

        Mathlib.translation(model.position, matrix_stack.next());
        Mathlib.rotation_z(model.rotation.data[2], matrix_stack.next());
        Mathlib.rotation_y(model.rotation.data[1], matrix_stack.next());
        Mathlib.rotation_x(model.rotation.data[0], matrix_stack.next());
        Mathlib.scale(model.scale, matrix_stack.next());

        matrix_stack.combine(matrices.MAT_MODEL);

        Mathlib.mat4_normal(matrices.MAT_MODEL, matrices.MAT_NORMAL);
        Mathlib.mat4_lookat(camera.eye, camera.target, camera.up, matrices.MAT_VIEW);

        Mathlib.mat4_mul(matrices.MAT_PROJECTION, matrices.MAT_VIEW, matrices.MAT_COMPOSED);
        Mathlib.mat4_mul(matrices.MAT_COMPOSED, matrices.MAT_MODEL, matrices.MAT_FINAL);
    }

    public void draw_batch()
    {
        if (use_multithreading && mt.batch_count > 0 &&
            ((mt.batch_count % Multithreading.TRIANGLE_BATCH == 0) || mesh.pointer >= mesh.face.length))
        {
            mt.batch_count = 0;
            mt.tile_pointer.set(0);

            synchronized (mt.worker_lock)
            {
                mt.running_workers.set(Multithreading.NUM_THREADS);

                for (int i = 0; i < Multithreading.NUM_THREADS; ++i)
                {
                    mt.worker_signal_sent[i] = true;
                    mt.workers[i].init_triangle(program, sampler);
                }

                mt.worker_lock.notifyAll();
            }

            synchronized (mt.main_lock)
            {
                while (!mt.main_signal_sent)
                {
                    try { mt.main_lock.wait(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }

                mt.main_signal_sent = false;
            }
        }
    }

    public void draw_model()
    {
        if (mesh == null)
            return;

        boolean is_wireframe = has_flag(FLAG_LINES) || has_flag(FLAG_POINTS);

        update_matrices();

        attrs = program.get_attrs();

        mesh.pointer = 0;
        sampler.mode(true);
        sampler.tex = texture;
        sampler.filtering = has_flag(FLAG_FILTERING);
        visible_tris = 0;

        int triangle_index = 1;
        Triangle triangle = new Triangle(attrs);

        while (mesh.pointer < mesh.face.length)
        {
            triangle.id = triangle_index++;

            program.reader(mesh, triangle, matrices, camera.eye);
            Mathlib.triangle_project(triangle, matrices.MAT_FINAL);
            Mathlib.triangle_viewport_transform(triangle, matrices.MAT_VIEWPORT);

            // Attribute division.
            for (int i = 4; i < 4 + attrs; ++i)
            {
                triangle.vertices[0].data[i] *= triangle.vertices[0].data[3];
                triangle.vertices[1].data[i] *= triangle.vertices[1].data[3];
                triangle.vertices[2].data[i] *= triangle.vertices[2].data[3];
            }

            triangle.area = triangle.area(0, 1, 2);

            if (!is_wireframe && ((flags & FLAG_BACKFACE_CULL) > 0) && triangle.area > 0)
                continue;

            if (has_flag(FLAG_TRIANGLES))
            {
                triangle.setup();
                draw_triangle(triangle);
            }
            else
            {
                if (has_flag(FLAG_LINES))
                {
                    draw_line(triangle.vertices[0].data[0], triangle.vertices[0].data[1],
                              triangle.vertices[1].data[0], triangle.vertices[1].data[1]);

                    draw_line(triangle.vertices[1].data[0], triangle.vertices[1].data[1],
                              triangle.vertices[2].data[0], triangle.vertices[2].data[1]);

                    draw_line(triangle.vertices[2].data[0], triangle.vertices[2].data[1],
                              triangle.vertices[0].data[0], triangle.vertices[0].data[1]);
                }

                if (has_flag(FLAG_POINTS))
                {
                    draw_point(triangle.vertices[0].data[0], triangle.vertices[0].data[1]);
                    draw_point(triangle.vertices[1].data[0], triangle.vertices[1].data[1]);
                    draw_point(triangle.vertices[2].data[0], triangle.vertices[2].data[1]);
                }
            }

            visible_tris++;
        }
    }

    public void draw_model_thread()
    {
        if (mesh == null)
            return;

        boolean is_wireframe = has_flag(FLAG_LINES) || has_flag(FLAG_POINTS);

        update_matrices();

        attrs = program.get_attrs();

        mesh.pointer = 0;
        sampler.mode(true);
        sampler.tex = texture;
        sampler.filtering = has_flag(FLAG_FILTERING);
        visible_tris = 0;

        int triangle_index = 1;
        Triangle triangle;
        mt.batch_count = 0;

        while (mesh.pointer < mesh.face.length)
        {
            triangle = mt.tribuffer[mt.batch_count++];
            triangle.id = triangle_index++;

            program.reader(mesh, triangle, matrices, camera.eye);
            Mathlib.triangle_project(triangle, matrices.MAT_FINAL);
            Mathlib.triangle_viewport_transform(triangle, matrices.MAT_VIEWPORT);

            // Attribute division.
            for (int i = 4; i < 4 + attrs; ++i)
            {
                triangle.vertices[0].data[i] *= triangle.vertices[0].data[3];
                triangle.vertices[1].data[i] *= triangle.vertices[1].data[3];
                triangle.vertices[2].data[i] *= triangle.vertices[2].data[3];
            }

            triangle.area = triangle.area(0, 1, 2);

            if (!is_wireframe && ((flags & FLAG_BACKFACE_CULL) > 0) && triangle.area > 0)
            {
                mt.batch_count--;
                draw_batch();
                continue;
            }

            if (has_flag(FLAG_TRIANGLES))
            {
                triangle.setup();
                draw_triangle_thread(triangle);
            }
            else
            {
                if (has_flag(FLAG_LINES))
                {
                    draw_line(triangle.vertices[0].data[0], triangle.vertices[0].data[1],
                              triangle.vertices[1].data[0], triangle.vertices[1].data[1]);

                    draw_line(triangle.vertices[1].data[0], triangle.vertices[1].data[1],
                              triangle.vertices[2].data[0], triangle.vertices[2].data[1]);

                    draw_line(triangle.vertices[2].data[0], triangle.vertices[2].data[1],
                              triangle.vertices[0].data[0], triangle.vertices[0].data[1]);
                }

                if (has_flag(FLAG_POINTS))
                {
                    draw_point(triangle.vertices[0].data[0], triangle.vertices[0].data[1]);
                    draw_point(triangle.vertices[1].data[0], triangle.vertices[1].data[1]);
                    draw_point(triangle.vertices[2].data[0], triangle.vertices[2].data[1]);
                }
            }

            visible_tris++;
            draw_batch();
        }
    }

    public void draw_triangle_thread(Triangle triangle)
    {
        scan_edges_thread(triangle.bottom_top, triangle.bottom_middle, triangle.reversed, triangle);
        scan_edges_thread(triangle.bottom_top, triangle.middle_top, triangle.reversed, triangle);
    }

    public void scan_edges_thread(Edge left_edge, Edge right_edge, boolean reversed, Triangle triangle)
    {
        Edge left = left_edge;
        Edge right = right_edge;

        if (reversed)
        {
            Edge tmp = left;
            left = right;
            right = tmp;
        }

        int sy = right_edge.startyi;
        int ey = right_edge.endyi;

        for (int y = sy; y < ey; ++y)
        {
            if (y >= 0 && y < this.height)
                draw_scanline_thread(y, left, right, triangle);

            left.step();
            right.step();
        }
    }

    public void draw_scanline_thread(int y, Edge left_edge, Edge right_edge, Triangle triangle)
    {
        int sx = (int) Math.ceil(left_edge.x);
        int ex = (int) Math.ceil(right_edge.x);
        float xoffset = sx - left_edge.x;

        ScanlineTaskData scanline = new ScanlineTaskData(attrs);

        scanline.y = y;
        scanline.sx = sx;
        scanline.ex = ex;
        scanline.gradients = triangle.gradients;
        scanline.triangle = triangle;

        // Attribute setup.
        for (int i = 0; i < scanline.count; ++i) {
            scanline.interp_base[i] = left_edge.interp[i] + xoffset * triangle.gradients.dx[i];
        }

        int tile_index = scanline.y >> mt.tile_shift;
        ScanlineTile tile = mt.tiles[tile_index];

        // if (tile_index > 5 && tile_index <= 7)
        // if (tile_index == 6)
        // if (sx == 594 && y == 380)
            tile.queue.add(scanline);
    }

    public void draw_triangle(Triangle triangle)
    {
        scan_edges(triangle.bottom_top, triangle.bottom_middle, triangle.reversed, triangle.gradients);
        scan_edges(triangle.bottom_top, triangle.middle_top, triangle.reversed, triangle.gradients);
    }

    public void scan_edges(Edge left_edge, Edge right_edge, boolean reversed, Gradients gradients)
    {
        Edge left = left_edge;
        Edge right = right_edge;

        if (reversed)
        {
            Edge tmp = left;
            left = right;
            right = tmp;
        }

        int sy = right_edge.startyi;
        int ey = right_edge.endyi;

        for (int y = sy; y < ey; ++y)
        {
            if (y >= 0 && y < this.height)
                draw_scanline(y, left, right, gradients);

            left.step();
            right.step();
        }
    }

    public void draw_scanline(int y, Edge left_edge, Edge right_edge, Gradients gradients)
    {
        int sx = (int) Math.ceil(left_edge.x);
        int ex = (int) Math.ceil(right_edge.x);
        float xoffset = sx - left_edge.x;

        int depth_pointer = (y * this.width + sx);
        int pixel = depth_pointer << 2;

        Scanline scanline = new Scanline(attrs);

        // Attribute setup.
        for (int i = 0; i < scanline.count; ++i) {
            scanline.interp_base[i] = left_edge.interp[i] + xoffset * gradients.dx[i];
        }

        for (int x = sx; x < ex; ++x)
        {
            if (x >= 0 && x < width)
            {
                float depth = scanline.interp_base[0];

                if (depth < buffer.depth[depth_pointer])
                {
                    buffer.depth[depth_pointer] = depth;

                    float one_over_w = 1.0f / scanline.interp_base[1];

                    // Attribute interpolation.
                    for (int i = 2; i < scanline.count; ++i) {
                        scanline.interp[i] = scanline.interp_base[i] * one_over_w;
                    }

                    program.shader(scanline.interp, sampler, pixel_color, x, y);

                    Mathlib.color_clamp(pixel_color);
                    // Mathlib.color_reinhard(pixel_color);
                    Mathlib.color_one_to_255(pixel_color);

                    if (x == 455 && y == 301)
                    {
                        int a = 0;
                    }

                    buffer.set_pixel(pixel,
                                     (byte) pixel_color.data[0],
                                     (byte) pixel_color.data[1],
                                     (byte) pixel_color.data[2]);
                }
            }

            // Advance the attributes.
            for (int i = 0; i < scanline.count; ++i) {
                scanline.interp_base[i] += gradients.dx[i];
            }

            pixel += 4;
            depth_pointer++;
        }
    }

    public void postprocessing()
    {
        int offset = 0;
        float u = 0, v = 0;

        sampler.mode(false);
        sampler.tex = buffer.rendered_texture;
        sampler.filtering = false;

        for (int y = 0; y < buffer.height; ++y)
        {
            u = 0;

            for (int x = 0; x < buffer.width; ++x)
            {
                ProgramFX.waves(sampler, u, 1 - v, pixel_color);
                Mathlib.color_one_to_255(pixel_color);
                buffer.set_pixel(offset,
                        (byte) pixel_color.data[0],
                        (byte) pixel_color.data[1],
                        (byte) pixel_color.data[2]);

                u += buffer.du;
                offset += 4;
            }

            v += buffer.dv;
        }
    }

    public void postprocessing_thread()
    {
        if (use_multithreading)
        {
            sampler.mode(false);
            sampler.tex = buffer.rendered_texture;
            sampler.filtering = false;

            mt.post_tile_pointer.set(0);

            synchronized (mt.worker_lock)
            {
                mt.running_workers.set(Multithreading.NUM_THREADS);

                for (int i = 0; i < Multithreading.NUM_THREADS; ++i)
                {
                    mt.worker_signal_sent[i] = true;
                    mt.workers[i].sampler = sampler;
                    mt.workers[i].current_state = Worker.STATE_POSTPROCESSING;
                }

                mt.worker_lock.notifyAll();
            }

            synchronized (mt.main_lock)
            {
                while (!mt.main_signal_sent)
                {
                    try { mt.main_lock.wait(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }

                mt.main_signal_sent = false;
            }
        }
    }
}
