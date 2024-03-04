package renderer;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import java.awt.event.KeyListener;

public class Window
{
    private JFrame _frame;
    private Canvas _canvas;
    private BufferStrategy _bs;
    private String title;

    public Window(int width, int height, String title)
    {
        this.title = title;
        Dimension size = new Dimension(width, height);

        this._canvas = new Canvas();
        this._canvas.addKeyListener(Keyboard.get_instance());
        this._canvas.setPreferredSize(size);
        this._canvas.setFocusable(true);
		this._canvas.requestFocus();

        this._frame = new JFrame();
        this._frame.add(this._canvas);
        this._frame.pack();
		this._frame.setResizable(false);
		this._frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this._frame.setLocationRelativeTo(null);
		this._frame.setTitle(title);
		// this._frame.setSize(width, height);
        this._frame.setVisible(true);

        this._canvas.createBufferStrategy(1);
        this._bs = this._canvas.getBufferStrategy();
        // this._bsg = this._bs.getDrawGraphics();
    }

    public void add_key_listener(KeyListener listener) {
        this._canvas.addKeyListener(listener);
    }

    public void flip_buffers(BufferedImage buffer, int width, int height)
    {
        Graphics tg = this._bs.getDrawGraphics();
        tg.drawImage(buffer, 0, 0, width, height, null);
        tg.dispose();
        this._bs.show();
    }
}