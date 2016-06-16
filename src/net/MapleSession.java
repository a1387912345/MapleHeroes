package net;

import java.nio.ByteOrder;

import client.MapleClient;
import constants.ServerConfig;
import constants.ServerConstants;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;
import server.shark.SharkPacket;
import tools.HexTool;

public class MapleSession extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext context) throws Exception {
    	final Channel socket = context.channel();
    	final MapleClient client = new MapleClient(socket);
    	
    	client.sendPacket(LoginPacket.getHello(client.getSendCrypto().getIv(), client.getReceiveCrypto().getIv()));
    	socket.attr(MapleClient.CLIENT_KEY).set(client);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
    	if (context == null || message == null) {
    		return;
    	}
    	final MapleClient client = context.channel().attr(MapleClient.CLIENT_KEY).get();
    	
    	if (client == null) {
    		return;
    	}
    	if (ServerConstants.LOG_SHARK) {
            final SharkPacket sp = new SharkPacket((byte[]) message, true);
            client.sl.log(sp);
        }
        final MaplePacketReader inPacket = new MaplePacketReader(Unpooled.wrappedBuffer((byte[]) message).order(ByteOrder.LITTLE_ENDIAN));
        final short header = inPacket.readShort();
        final MaplePacketHandler packetHandler = PacketProcessor.getInstance().getHandler(header);
        
        if (packetHandler != null && packetHandler.validateState(client)) {
        	if (ServerConfig.logPackets && !RecvPacketOpcode.isSpam(packetHandler.getRecvOpcode())) {
        		printRecvLog(packetHandler.getRecvOpcode().name(), header, (byte[]) message);
        	}
        	try {
        		packetHandler.handlePacket(inPacket, client, client.getCharacter());
        	} catch (Exception ex) {
        		System.err.println("[Error] An error occured when trying to handle the packet " + HexTool.getOpcodeToString(header));
            	ex.printStackTrace();
        	}
        } else {
        	String recvName = "UNKNOWN";
        	for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
        		if (recv.getOpcode() == header) {
        			recvName = recv.name();
        		}
        	}
        	printRecvLog(recvName, header, (byte[]) message);
        }  
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
    	if (event instanceof IdleStateEvent) {
    		IdleStateEvent idleStateEvent = (IdleStateEvent) event;
    		if (idleStateEvent.state() == IdleState.READER_IDLE) {
    			System.out.println("No response from client. Closing channel socket.");
    			context.close();
    		} else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
    			final MapleClient client = context.channel().attr(MapleClient.CLIENT_KEY).get();
    			client.sendPing();
    		}
    	}
    }
    
	@Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
		final MapleClient client = context.channel().attr(MapleClient.CLIENT_KEY).get();
		
		if (client != null) {
            try {
                client.disconnect(true, true);
            } finally {
                context.close();
                context.channel().attr(MapleClient.CLIENT_KEY).remove();
            }
		}
		System.out.println("Session Disconnected");
        super.channelInactive(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
    
    public void printRecvLog(String recv, short header, byte[] message) {
    	String tab = "";
		for (int i = 4; i > Integer.valueOf(recv.length() / 8); i--) {
            tab += "\t";
        }
		System.out.println("[Recv]\t" + recv + tab + "|     " + HexTool.getOpcodeToString(header) + "\t|\t" + HexTool.toString((byte[]) message));
    }
    
}
