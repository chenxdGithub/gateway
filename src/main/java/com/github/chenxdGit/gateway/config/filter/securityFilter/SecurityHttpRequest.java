package com.github.chenxdGit.gateway.config.filter.securityFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;

import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Flux;

public class SecurityHttpRequest extends ServerHttpRequestDecorator {
	@SuppressWarnings("rawtypes")
	Map bodyMap = new HashMap();
	private boolean subscribe = false;
	private int contentLength;
	public SecurityHttpRequest(ServerHttpRequest delegate) {
		super(delegate);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Flux<DataBuffer> getBody() {
		 Flux<DataBuffer> body = getDelegate().getBody();
		 if(subscribe==false) {
		 body.buffer().subscribe(dataBuffers -> {
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
			 String desEncrypt = SecurityUtils.decrypt(outputStream.toString());
			 byte[] newRs  =desEncrypt.getBytes();
			 bodyMap.put("body", Flux.just(stringBuffer(newRs)));
			 contentLength = newRs.length;
             this.subscribe = true;
         });
		 }
         return(Flux<DataBuffer>) bodyMap.get("body");
	}
	private DataBuffer stringBuffer(byte[] bytes){
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
	@Override
	public URI getURI() {
		URI uri = getDelegate().getURI();
		String query = uri.getQuery();
		String desEncrypt = SecurityUtils.decrypt(query);
		URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(desEncrypt).build().toUri();
		return newUri;
	}
	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders newHeaders = new  HttpHeaders();
		HttpHeaders oldHeaders = getDelegate().getHeaders();
		newHeaders.putAll(oldHeaders);
		this.getBody();
		newHeaders.setContentLength(contentLength);
		return newHeaders;
	}
}