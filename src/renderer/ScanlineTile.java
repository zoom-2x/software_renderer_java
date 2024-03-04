package renderer;

import java.util.LinkedList;

public class ScanlineTile
{
    public int y;
    public ScreenBuffer buffer;
    public LinkedList<ScanlineTaskData> queue = new LinkedList<ScanlineTaskData>();

    public ScanlineTile(ScreenBuffer buffer, int y)
    {
        this.buffer = buffer;
        this.y = y;
    }
}
