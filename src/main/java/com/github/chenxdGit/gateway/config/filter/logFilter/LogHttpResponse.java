package com.github.chenxdGit.gateway.config.filter.logFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
public class LogHttpResponse extends ServerHttpResponseDecorator {
	public LogHttpResponse(ServerHttpResponse delegate) {
		super(delegate);
	}
	
    @SuppressWarnings("unchecked")
	public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
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
			 log.info(outputStream.toString());
            return stringBuffer(outputStream.toByteArray());
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