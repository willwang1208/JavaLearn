package org.whb.springmvc.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.whb.springmvc.repository.domain.Author;

public interface AuthorMapper {

    @Insert("INSERT INTO author (id, name, email) VALUES (#{id}, #{name}, #{email})")
    public int insertAuthor(Author author);
    
    @Update("UPDATE author SET name = #{name}, email = #{email} WHERE id = #{id}")
    public int updateAuthor(Author author);
    
    @Update("UPDATE author SET delFlag = 1 WHERE id = ${_parameter}")
    public int deleteAuthor(long authorId);
    
    @Select("SELECT id, name, email, delFlag FROM author WHERE id = ${_parameter}")
    public Author getAuthor(long authorId);
}
