package net.server.loginauth;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class LoginAuthDecoder extends ReplayingDecoder<Void> {
	
	long[] xorTable = new long[] {
			0x040FC1578, 0x0113B6C1F, 0x08389CA19, 0x0E2196CD8,
			0x074901489, 0x04AAB1566, 0x07B8C12A0, 0x00018FFCD,
			0x0CCAB704B, 0x07B5A8C0F, 0x0AA13B891, 0x0DE419807,
			0x012FFBCAE, 0x05F5FBA34, 0x010F5AC99, 0x0B1C1DD01
	};

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> objects) throws Exception {		
		// Header
		short packetSize = in.readShort();
		short opcode = in.readShort();
		//System.out.println("Header received. PacketSize " + packetSize + " Opcode " + opcode);

		// Body
		byte[] decryptedBuffer = new byte[packetSize - 16];
		
		byte checkCode = in.readByte();
		int length = readSmallSize(in);
		byte flag = in.readByte();
		int originalSize = readSmallSize(in);
		long xorValue = in.readUnsignedInt();
		in.readInt();
		in.readBytes(decryptedBuffer);
		//System.out.println(checkCode + " " + length + " " + flag + " " + originalSize + " " + xorValue);
		
		if ((flag & 0x02) != 0) {
			decryptedBuffer = decrypt(decryptedBuffer, xorValue);
		}
		
		byte[] decodedPacket = new byte[decryptedBuffer.length + 2];
		decodedPacket[0] = (byte) opcode;
		System.arraycopy(decryptedBuffer, 0, decodedPacket, 2, decryptedBuffer.length);
		//System.out.println(HexTool.toString(decodedPacket));
		objects.add(decodedPacket);
	}
	
	private int readSmallSize(ByteBuf in) {
		byte a = in.readByte();
		byte b = in.readByte();
		byte c = in.readByte();
		
		return (int) (((int) a) << 16 | ((int) b) << 8 | ((int) c) << 0);
	}
	
	private byte[] decrypt(byte[] buffer, long seed) {
		long temp = 0;
		long temp2 = 0;
		
		byte[] output = new byte[buffer.length];
		
		for (int i = 0; i < buffer.length/4; i++) {
			temp2 = Integer.toUnsignedLong(ByteBuffer.wrap(buffer, i*4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
			temp2 ^= Integer.toUnsignedLong((int)(temp ^ xorTable[i & 15] ^ seed));
			setBytes(temp2, output, i*4);
			temp = (int) temp2;
		}
		
		return output;
	}
	
	private void setBytes(long input, byte[] buffer, int offset) {
		buffer[offset + 0] = (byte) ((input >> 0) & 0xFF);
		buffer[offset + 1] = (byte) ((input >> 8) & 0xFF);
		buffer[offset + 2] = (byte) ((input >> 16) & 0xFF);
		buffer[offset + 3] = (byte) ((input >> 24) & 0xFF);
	}

}
