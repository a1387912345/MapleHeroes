package net.netty;

import java.awt.Point;
import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import tools.HexTool;

public class MaplePacketReader {
	
	private final ByteBuf buffer;
	
	public MaplePacketReader(ByteBuf buffer) {
		this.buffer = buffer;
	}
	
    public void seek(long offset) {
        buffer.readerIndex((int)offset);
    }

    public long getPosition() {
        return buffer.readerIndex();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public char readChar() {
        return buffer.readChar();
    }

    public short readShort() {
        return buffer.readShort();
    }

    public int readInt() {
        return buffer.readInt();
    }

    public long readLong() {
        return buffer.readLong();
    }

    public void skip(int num) {
        buffer.skipBytes(num);
    }

    public byte[] read(int num) {
        byte[] ret = new byte[num];
        buffer.readBytes(ret);
        return ret;
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public String readAsciiString(int n) {
        char[] string = new char[n];
        for(int x = 0; x < n; ++x)
            string[x] = (char)readByte();
        return String.valueOf(string);
    }

	public String readNullTerminatedAsciiString() {
        ByteBuf buf = Unpooled.directBuffer().order(ByteOrder.LITTLE_ENDIAN);
        byte b;
        while ((b = readByte()) != 0)
            buf.writeByte(b);
        byte[] bytes = buf.array();
        char[] string = new char[bytes.length];
        for(int x = 0; x < string.length; ++x)
            string[x] = (char)bytes[x];
        return String.valueOf(string);
    }

    public String readMapleAsciiString() {
        return readAsciiString(readShort());
    }

    public long getBytesRead() {
        return buffer.readerIndex();
    }

    public long available() {
        return buffer.readableBytes();
    }

	public int readUShort() {
		return buffer.readUnsignedShort();
	}
	
	public Point readPos() {
        final int x = readShort();
        final int y = readShort();
        return new Point(x, y);
    }
	
	@Override
	public String toString() {
		return HexTool.toString(buffer.array());
	}

}
