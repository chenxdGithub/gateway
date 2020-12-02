package com.github.chenxdGit.gateway.config.filter.logFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
@Slf4j
public class LogHttpRequest extends ServerHttpRequestDecorator {
	
	public Long  startTime =System.currentTimeMillis();
	
	public LogHttpRequest(ServerHttpRequest delegate) {
		super(delegate);
	}
	@Override
	public Flux<DataBuffer> getBody() {
			Flux<DataBuffer> fluxBody = getDelegate().getBody();
			 Flux<DataBuffer> map = fluxBody.buffer().map(dataBuffers ->{   
				 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				 dataBuffers.forEach(i -> {
	            byte[] array = new byte[i.readableByteCount()];
	            	i.read(array);
	            	DataBufferUtils.release(i);
	            	outputStream.write(array, 0, array.length);
				 });
				 try {
					 if (outputStream != null) {
						 outputStream.close();
					 }
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
				 log.info(outputStream.toString());
	            return stringBuffer(outputStream.toByteArray());
			 });
			 return map;
	}
	private DataBuffer stringBuffer(byte[] bytes){
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

}