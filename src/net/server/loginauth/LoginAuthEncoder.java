package net.server.loginauth;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LoginAuthEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		byte[] input = (byte[]) message;
		short size = (short) (1 + 3 + 1 + 3 + 4 + 4 + input.length - 2);
		
		ByteBuf buffer = Unpooled.buffer(size, size + 4);
		buffer.writeShort(size);
		buffer.writeShort(input[0]);
		buffer.writeByte(0x18);
		buffer.writeBytes(writeSmallSize(size));
		buffer.writeByte(0);
		buffer.writeBytes(writeSmallSize(size - 12));
		buffer.writeInt(0xDEADB00B);
		buffer.writeInt(0xAABBCCDD);
		buffer.writeBytes(input, 2, input.length - 2);

		//System.out.println(HexTool.toString(buffer.array()));

		out.writeBytes(buffer.array());
	}
	
	private byte[] writeSmallSize(int input) {
		return new byte[] {(byte)(input >> 16), (byte)(input >> 8), (byte)(input >> 0) };
	}

}
