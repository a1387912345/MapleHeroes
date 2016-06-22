package net.netty;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import client.MapleClient;
import constants.ServerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.MapleCrypto;
import server.shark.SharkPacket;

public class MaplePacketEncoder extends MessageToByteEncoder<Object> {

	@Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
		
		if (client != null) {
			MapleCrypto sendCrypto = client.getSendCrypto();
			
	        byte[] input = (byte[]) message;
	        
	        if (ServerConstants.LOG_SHARK) {
                final SharkPacket sp = new SharkPacket((byte[]) message, false);
                client.sl.log(sp);
            }
	        
	        byte[] unencrypted = Arrays.copyOf(input, input.length);
	        byte[] encrypted = new byte[unencrypted.length + 4];
	        
	        Lock mutex = client.getLock();
	        mutex.lock();
	        try {
	        	byte[] header = sendCrypto.getPacketHeader(unencrypted.length);
	        	sendCrypto.crypt(unencrypted);
	        	System.arraycopy(header, 0, encrypted, 0, 4);
	        } finally {
	        	mutex.unlock();
	        }
	        System.arraycopy(unencrypted, 0, encrypted, 4, unencrypted.length);
	        out.writeBytes(encrypted);
		} else {
			out.writeBytes((byte[]) message);
		}
    }
}
