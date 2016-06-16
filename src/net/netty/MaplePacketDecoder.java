package net.netty;

import java.util.List;

import client.MapleClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.MapleCrypto;

public class MaplePacketDecoder extends ReplayingDecoder<Void> {

	@Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> objects) throws Exception {
		MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
		int storedLength = -1;
		
		if (client != null) {
			if (storedLength == -1) {
				if (in.readableBytes() >= 4) {
					int h = in.readInt();
					if (!client.getReceiveCrypto().checkPacket(h)) {
						client.close();
						return;
					}
					storedLength = MapleCrypto.getPacketLength(h);
				} else {
					return;
				}
			}
			if (in.readableBytes() >= storedLength) {
				byte[] header = new byte[storedLength];
				in.readBytes(header);
				storedLength = -1;
				
				client.getReceiveCrypto().crypt(header);
				objects.add(header);
			}
		}
    }
}
