package net;

import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import net.netty.LittleEndianByteBufAllocator;
import net.netty.MaplePacketDecoder;
import net.netty.MaplePacketEncoder;

public class Acceptor extends ChannelInitializer<SocketChannel> implements Runnable {
	
	private EventLoopGroup acceptorGroup, clientGroup;
	private Channel acceptor;
	private final int channel;
	
	private SocketAddress socketAddress;
	
	public Acceptor(SocketAddress socketAddress, int channel) {
		this.socketAddress = socketAddress;
		this.channel = channel;
	}
	
	public Acceptor(SocketAddress socketAddress) {
		this(socketAddress, -1);
	}

	@Override
	public void run() {
		acceptorGroup = new NioEventLoopGroup(16);
		clientGroup = new NioEventLoopGroup(16);
		
		acceptor = new ServerBootstrap()
				.group(acceptorGroup, clientGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(this)
				.option(ChannelOption.SO_BACKLOG, 64)
				.option(ChannelOption.ALLOCATOR, new LittleEndianByteBufAllocator(UnpooledByteBufAllocator.DEFAULT))
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.bind(socketAddress).syncUninterruptibly().channel();
	}
	
	public void stop() {
		acceptor.close();
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline().addLast(new IdleStateHandler(20, 20, 0));
		socketChannel.pipeline().addLast(new MaplePacketDecoder(), new MapleSession(), new MaplePacketEncoder());
	}

}
