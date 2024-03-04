package renderer.struct;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import renderer.Utils;

public abstract class Struct
{
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int INT = 3;
    public static final int LONG = 4;
    public static final int FLOAT = 5;
    public static final int DOUBLE = 6;
    public static final int ASCII20 = 7;

    protected boolean force_little_endian = false;
    protected int pointer;
    public byte[] _buffer;

    protected ArrayList<Integer> _format = new ArrayList<Integer>();

    public int get_bytes()
    {
        int bytes = 0;

        for (Integer f : _format)
        {
            if (f == Struct.BYTE)
                bytes += 1;
            else if (f == Struct.SHORT)
                bytes += 2;
            else if (f == Struct.INT)
                bytes += 4;
            else if (f == Struct.LONG)
                bytes += 8;
            else if (f == Struct.FLOAT)
                bytes += 4;
            else if (f == Struct.DOUBLE)
                bytes += 8;
            // Fixed size UTF-16 strings, 20 characters.
            else if (f == Struct.ASCII20)
                bytes += 20;
        }

        return bytes;
    }

    // public void read(RandomAccessFile file) throws IOException
    public void read(DataInput file) throws IOException
    {
        int format_index = 0;
        Field[] fields = this.getClass().getDeclaredFields();

        try
        {
            if (force_little_endian)
            {
                int bytes = get_bytes();

                StructBuffer buffer = new StructBuffer(bytes);
                file.readFully(buffer.data);

                for (int i = 0; i < fields.length; ++i)
                {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();

                    if ((modifiers & Modifier.STATIC) > 0)
                        continue;

                    int field_format = _format.get(format_index++);

                    if (field_format == Struct.BYTE)
                        field.setByte(this, buffer.read_byte());
                    else if (field_format == Struct.SHORT)
                        field.setShort(this, buffer.read_short());
                    else if (field_format == Struct.INT)
                        field.setInt(this, buffer.read_int());
                    else if (field_format == Struct.LONG)
                        field.setLong(this, buffer.read_long());
                    else if (field_format == Struct.FLOAT)
                        field.setFloat(this, buffer.read_float());
                    else if (field_format == Struct.DOUBLE)
                        field.setDouble(this, buffer.read_double());
                    else if (field_format == Struct.ASCII20)
                    {
                        byte[] ascii = new byte[20];
                        buffer.read(ascii);
                        field.set(this, new String(ascii).trim());
                    }
                }
            }
            else
            {
                for (int i = 0; i < fields.length; ++i)
                {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();

                    if ((modifiers & Modifier.STATIC) > 0)
                        continue;

                    int field_format = _format.get(format_index++);

                    if (field_format == Struct.BYTE)
                        field.setByte(this, file.readByte());
                    else if (field_format == Struct.SHORT)
                        field.setShort(this, file.readShort());
                    else if (field_format == Struct.INT)
                        field.setInt(this, file.readInt());
                    else if (field_format == Struct.LONG)
                        field.setLong(this, file.readLong());
                    else if (field_format == Struct.FLOAT)
                        field.setFloat(this, file.readFloat());
                    else if (field_format == Struct.DOUBLE)
                        field.setDouble(this, file.readDouble());
                    else if (field_format == Struct.ASCII20)
                    {
                        byte[] ascii = new byte[20];
                        file.readFully(ascii);
                        field.set(this, new String(ascii).trim());
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    // public void write(RandomAccessFile file) throws IOException
    public void write(DataOutput file) throws IOException
    {
        int format_index = 0;
        Field[] fields = this.getClass().getDeclaredFields();

        try
        {
            if (force_little_endian)
            {
                int bytes = get_bytes();
                StructBuffer buffer = new StructBuffer(bytes);

                for (int i = 0; i < fields.length; ++i)
                {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();

                    if ((modifiers & Modifier.STATIC) > 0)
                        continue;

                    int field_format = _format.get(format_index++);

                    if (field_format == Struct.BYTE)
                        buffer.write_byte(field.getByte(this));
                    else if (field_format == Struct.SHORT)
                        buffer.write_short(field.getShort(this));
                    else if (field_format == Struct.INT)
                        buffer.write_int(field.getInt(this));
                    else if (field_format == Struct.LONG)
                        buffer.write_long(field.getLong(this));
                    else if (field_format == Struct.FLOAT)
                        buffer.write_float(field.getFloat(this));
                    else if (field_format == Struct.DOUBLE)
                        buffer.write_double(field.getDouble(this));
                    else if (field_format == Struct.ASCII20)
                    {
                        String str = (String) field.get(this);
                        byte[] ascii = Utils.string_to_ascii(str, 20);
                        buffer.write(ascii);
                    }
                }

                file.write(buffer.data);
            }
            else
            {
                for (int i = 0; i < fields.length; ++i)
                {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();

                    if ((modifiers & Modifier.STATIC) > 0)
                        continue;

                    int field_format = _format.get(format_index++);

                    if (field_format == Struct.BYTE)
                        file.writeByte(field.getByte(this));
                    else if (field_format == Struct.SHORT)
                        file.writeShort(field.getShort(this));
                    else if (field_format == Struct.INT)
                        file.writeInt(field.getInt(this));
                    else if (field_format == Struct.LONG)
                        file.writeLong(field.getLong(this));
                    else if (field_format == Struct.FLOAT)
                        file.writeFloat(field.getFloat(this));
                    else if (field_format == Struct.DOUBLE)
                        file.writeDouble(field.getDouble(this));
                    else if (field_format == Struct.ASCII20)
                    {
                        String str = (String) field.get(this);
                        byte[] ascii = Utils.string_to_ascii(str, 20);
                        file.write(ascii);
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public void seek(int offset) {
        pointer = offset;
    }

    public void skip_bytes(int bytes) {
        pointer += bytes;
    }

    public void read(byte[] b)
    {
        for (int i = 0; i < b.length; ++i) {
            b[i] = _buffer[pointer++];
        }
    }

    public short read_short()
    {
        short val = (short) (((this._buffer[pointer + 1] & 0xff) << 8) | (this._buffer[pointer + 0] & 0xff));
        pointer += 2;

        return val;
    }

    public int read_int()
    {
        int val = (int) (((this._buffer[pointer + 3] & 0xff) << 24) |
                         ((this._buffer[pointer + 2] & 0xff) << 16) |
                         ((this._buffer[pointer + 1] & 0xff) << 8) |
                          (this._buffer[pointer + 0] & 0xff));

        pointer += 4;

        return val;
    }

    public long read_long()
    {
        long val = (long) (((this._buffer[pointer + 7] & 0xff) << 56) |
                           ((this._buffer[pointer + 6] & 0xff) << 48) |
                           ((this._buffer[pointer + 5] & 0xff) << 40) |
                           ((this._buffer[pointer + 4] & 0xff) << 32) |
                           ((this._buffer[pointer + 3] & 0xff) << 24) |
                           ((this._buffer[pointer + 2] & 0xff) << 16) |
                           ((this._buffer[pointer + 1] & 0xff) << 8) |
                            (this._buffer[pointer + 0] & 0xff));

        pointer += 8;

        return val;
    }

    public float read_float()
    {
        float val = (float) (((this._buffer[pointer + 3] & 0xff) << 24) |
                             ((this._buffer[pointer + 2] & 0xff) << 16) |
                             ((this._buffer[pointer + 1] & 0xff) << 8) |
                              (this._buffer[pointer + 0] & 0xff));

        pointer += 4;

        return val;
    }

    public void write(byte[] b)
    {
        for (int i = 0; i < b.length; ++i) {
            _buffer[pointer++] = b[i];
        }
    }

    public void write_short(short val)
    {
        _buffer[pointer + 0] = (byte) (val & 0xff);
        _buffer[pointer + 1] = (byte) ((val >> 8) & 0xff);

        pointer += 2;
    }

    public void write_int(int val)
    {
        _buffer[pointer + 0] = (byte) (val & 0xff);
        _buffer[pointer + 1] = (byte) ((val >> 8) & 0xff);
        _buffer[pointer + 2] = (byte) ((val >> 16) & 0xff);
        _buffer[pointer + 3] = (byte) ((val >> 24) & 0xff);

        pointer += 4;
    }

    public void write_long(long val)
    {
        _buffer[pointer + 0] = (byte) (val & 0xff);
        _buffer[pointer + 1] = (byte) ((val >> 8) & 0xff);
        _buffer[pointer + 2] = (byte) ((val >> 16) & 0xff);
        _buffer[pointer + 3] = (byte) ((val >> 24) & 0xff);
        _buffer[pointer + 4] = (byte) ((val >> 32) & 0xff);
        _buffer[pointer + 5] = (byte) ((val >> 40) & 0xff);
        _buffer[pointer + 6] = (byte) ((val >> 48) & 0xff);
        _buffer[pointer + 7] = (byte) ((val >> 56) & 0xff);

        pointer += 8;
    }
}
