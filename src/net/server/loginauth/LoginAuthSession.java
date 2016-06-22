package net.server.loginauth;

import java.nio.ByteOrder;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.netty.MaplePacketReader;
import net.netty.MaplePacketWriter;
import net.packet.LoginAuthPacket;

public class LoginAuthSession extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object message) throws Exception {
		Channel channel = ctx.channel();

		final MaplePacketReader pr = new MaplePacketReader(Unpooled.wrappedBuffer((byte[]) message).order(ByteOrder.LITTLE_ENDIAN));
        final short header = pr.readShort();
        //System.out.println("[Recv] " + header);
        switch(header) {
	        case 0x33:
	        	pr.readInt();
	    		String username = pr.readLoginAuthString();
	    		channel.writeAndFlush(LoginAuthPacket.handleLogin(username));
	        	break;
	        case 0x2D:
	        	channel.writeAndFlush(LoginAuthPacket.handleLogin2());
	        	break;
	        case 0x35:
	        	channel.writeAndFlush(LoginAuthPacket.handleLogin3());
	        	break;
        }
	}

}
