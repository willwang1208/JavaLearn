package org.whb.springmvc.repository.mapper;

import java.util.List;

import org.whb.springmvc.repository.domain.Article;

public interface ArticleMapper {

    public List<Article> getAllArticles();
    
    public int insertArticle(Article article);
    
    public int updateArticle(Article article);
    
    public int deleteArticle(long articleId);
    
    public Article getArticle(long articleId);
}
