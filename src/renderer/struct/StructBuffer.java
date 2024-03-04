package renderer.struct;

public class StructBuffer
{
    public byte[] data;
    public int pointer;

    public StructBuffer(int size)
    {
        data = new byte[size];
        pointer = 0;
    }

    public void read(byte[] buffer)
    {
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte) (data[pointer++] & 0xff);
        }
    }

    public byte read_byte()
    {
        byte val = (byte) (data[pointer++] & 0xff);
        return val;
    }

    public short read_short()
    {
        short val = (short) (((data[pointer + 1] & 0xff) << 8) | (data[pointer + 0] & 0xff));
        pointer += 2;

        return val;
    }

    public int read_int()
    {
        int val = (int) (((data[pointer + 3] & 0xff) << 24) |
                         ((data[pointer + 2] & 0xff) << 16) |
                         ((data[pointer + 1] & 0xff) << 8) |
                          (data[pointer + 0] & 0xff));

        pointer += 4;

        return val;
    }

    public long read_long()
    {
        long val = (long) (((data[pointer + 7] & 0xff) << 56) |
                           ((data[pointer + 6] & 0xff) << 48) |
                           ((data[pointer + 5] & 0xff) << 40) |
                           ((data[pointer + 4] & 0xff) << 32) |
                           ((data[pointer + 3] & 0xff) << 24) |
                           ((data[pointer + 2] & 0xff) << 16) |
                           ((data[pointer + 1] & 0xff) << 8) |
                            (data[pointer + 0] & 0xff));

        pointer += 8;

        return val;
    }

    public float read_float()
    {
        int float_bits = (((data[pointer + 3] & 0xff) << 24) |
                          ((data[pointer + 2] & 0xff) << 16) |
                          ((data[pointer + 1] & 0xff) << 8) |
                           (data[pointer + 0] & 0xff));

        float val = Float.intBitsToFloat(float_bits);
        pointer += 4;

        return val;
    }

    public double read_double()
    {
        long double_bits = (((data[pointer + 7] & 0xff) << 56) |
                            ((data[pointer + 6] & 0xff) << 48) |
                            ((data[pointer + 5] & 0xff) << 40) |
                            ((data[pointer + 4] & 0xff) << 32) |
                            ((data[pointer + 3] & 0xff) << 24) |
                            ((data[pointer + 2] & 0xff) << 16) |
                            ((data[pointer + 1] & 0xff) << 8) |
                             (data[pointer + 0] & 0xff));

        double val = Double.longBitsToDouble(double_bits);
        pointer += 8;

        return val;
    }

    public void write(byte[] buffer)
    {
        for (int i = 0; i < buffer.length; ++i) {
            data[pointer++] = (byte) (buffer[i] & 0xff);
        }
    }

    public void write_byte(byte val)
    {
        data[pointer] = (byte) (val & 0xff);
        pointer += 1;
    }

    public void write_short(short val)
    {
        data[pointer + 0] = (byte) (val & 0xff);
        data[pointer + 1] = (byte) ((val >> 8) & 0xff);

        pointer += 2;
    }

    public void write_int(int val)
    {
        data[pointer + 0] = (byte) (val & 0xff);
        data[pointer + 1] = (byte) ((val >> 8) & 0xff);
        data[pointer + 2] = (byte) ((val >> 16) & 0xff);
        data[pointer + 3] = (byte) ((val >> 24) & 0xff);

        pointer += 4;
    }

    public void write_long(long val)
    {
        data[pointer + 0] = (byte) (val & 0xff);
        data[pointer + 1] = (byte) ((val >> 8) & 0xff);
        data[pointer + 2] = (byte) ((val >> 16) & 0xff);
        data[pointer + 3] = (byte) ((val >> 24) & 0xff);
        data[pointer + 4] = (byte) ((val >> 32) & 0xff);
        data[pointer + 5] = (byte) ((val >> 40) & 0xff);
        data[pointer + 6] = (byte) ((val >> 48) & 0xff);
        data[pointer + 7] = (byte) ((val >> 56) & 0xff);

        pointer += 8;
    }

    public void write_float(float val)
    {
        int int_bits = Float.floatToIntBits(val);

        data[pointer + 0] = (byte) (int_bits & 0xff);
        data[pointer + 1] = (byte) ((int_bits >> 8) & 0xff);
        data[pointer + 2] = (byte) ((int_bits >> 16) & 0xff);
        data[pointer + 3] = (byte) ((int_bits >> 24) & 0xff);

        pointer += 4;
    }

    public void write_double(double val)
    {
        long long_bits = Double.doubleToLongBits(val);

        data[pointer + 0] = (byte) (long_bits & 0xff);
        data[pointer + 1] = (byte) ((long_bits >> 8) & 0xff);
        data[pointer + 2] = (byte) ((long_bits >> 16) & 0xff);
        data[pointer + 3] = (byte) ((long_bits >> 24) & 0xff);
        data[pointer + 4] = (byte) ((long_bits >> 32) & 0xff);
        data[pointer + 5] = (byte) ((long_bits >> 40) & 0xff);
        data[pointer + 6] = (byte) ((long_bits >> 48) & 0xff);
        data[pointer + 7] = (byte) ((long_bits >> 56) & 0xff);

        pointer += 8;
    }
}
