package com.xutest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class Child extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline ripe=ch.pipeline();
		ripe.addLast("http",new HttpServerCodec());
	}



}
