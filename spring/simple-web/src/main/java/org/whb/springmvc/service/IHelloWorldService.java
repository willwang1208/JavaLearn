package org.whb.springmvc.service;

import java.util.List;
import java.util.Map;

import org.whb.springmvc.entity.Book;
import org.whb.springmvc.entity.Report;
import org.whb.springmvc.entity.User;

public interface IHelloWorldService {
	
    public List<Book> getBooks();
    
    public User getUser();
    
    public Report getReport();
    
    public Map<String, String> getMapData();
}
