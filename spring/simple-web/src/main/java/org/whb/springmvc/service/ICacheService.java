package org.whb.springmvc.service;

import org.whb.springmvc.entity.User;

public interface ICacheService {
    
    public User save(User user);

    public User remove(final int id);

    public void removeAll();

    public User findById(final int id);
}
