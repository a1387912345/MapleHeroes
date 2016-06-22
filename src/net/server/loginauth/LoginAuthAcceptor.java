package net.server.loginauth;
import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class LoginAuthAcceptor extends ChannelInitializer<SocketChannel> implements Runnable {
	
	private EventLoopGroup acceptorGroup, clientGroup;
	private Channel acceptor;
	
	private SocketAddress socketAddress;
	
	public LoginAuthAcceptor(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
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
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.bind(socketAddress).syncUninterruptibly().channel();
	}
	
	public void stop() {
		acceptor.close();
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline().addLast(new LoginAuthDecoder(), new LoginAuthSession(), new LoginAuthEncoder());
	}

}
