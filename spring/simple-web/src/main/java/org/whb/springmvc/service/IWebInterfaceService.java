package org.whb.springmvc.service;

public interface IWebInterfaceService {
	
	public String special(byte[] content);
	
	public String save(byte[] content);
	
	public String query(byte[] content);
}
