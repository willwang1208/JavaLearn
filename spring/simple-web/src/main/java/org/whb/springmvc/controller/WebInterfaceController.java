package org.whb.springmvc.controller;

import java.io.DataInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/webi")
public class WebInterfaceController {
    
    @RequestMapping(value = "/special", method = RequestMethod.POST)
	public void common(HttpServletRequest request, HttpServletResponse response) {
		int length = request.getContentLength();
		if(length <= 0){
			return;
		}
		
		byte[] content = new byte[length];
		try {
			DataInputStream dis = new DataInputStream(request.getInputStream());
			dis.readFully(content);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		//将content解码解压，按约定反序列化成json、pb、fb对象
		
		//调用业务接口，得到响应结果
		
		//将响应结果按约定序列化成json、pb、fb对象，压缩转码
		byte[] result = "test".getBytes();
		
		if(result != null){
			try {
				response.getOutputStream().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
//  @RequestMapping("/proto/read")
//  public ResponseEntity<UserProtos.User> protoRead() {
//      return ResponseEntity.ok(UserProtos.User.newBuilder().setId(1).setName("zhangsan").build());
//  }
//
//
//  @RequestMapping("/proto/write")
//  public ResponseEntity<UserProtos.User> protoRead(RequestEntity<UserProtos.User> requestEntity) {
//      System.out.println("server===\n" + requestEntity.getBody());
//      return ResponseEntity.ok(requestEntity.getBody());
//  }
    
}
