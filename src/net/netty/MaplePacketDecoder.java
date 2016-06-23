package net.netty;

import java.util.List;

import client.MapleClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.MapleCrypto;

public class MaplePacketDecoder extends ReplayingDecoder<Void> {
	
	public static class DecoderState {

        public int packetlength = -1;
    }

	@Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> objects) throws Exception {
		final DecoderState decoderState = ctx.channel().attr(MapleClient.DECODER_STATE).get(); 
		final MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
		
		if (decoderState.packetlength == -1) {
			if (in.readableBytes() >= 4) {
				final int packetHeader = in.readInt();
				if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
					client.close();
					return;
				}
				decoderState.packetlength = MapleCrypto.getPacketLength(packetHeader);
			} else {
				return;
			}
		}
		if (in.readableBytes() >= decoderState.packetlength) {
			byte[] decryptedPacket = new byte[decoderState.packetlength];
			in.readBytes(decryptedPacket);
			decoderState.packetlength = -1;
			
			client.getReceiveCrypto().crypt(decryptedPacket);
			objects.add(decryptedPacket);
		}
    }
}
