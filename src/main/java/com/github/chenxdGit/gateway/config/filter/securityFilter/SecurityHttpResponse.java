package com.github.chenxdGit.gateway.config.filter.securityFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;

import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SecurityHttpResponse extends ServerHttpResponseDecorator {
	public SecurityHttpResponse(ServerHttpResponse delegate) {
		super(delegate);
	}
	
    @SuppressWarnings("unchecked")
	public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    	//加密请求体
		 Flux<DataBuffer> fluxBody = (Flux<DataBuffer>)body;
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
		     byte[] newRs =SecurityUtils.encrypt(outputStream.toString()).getBytes();
            return stringBuffer(newRs);
		 });
 		return super.writeWith(map);
    }
	private DataBuffer stringBuffer(byte[] bytes){
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}