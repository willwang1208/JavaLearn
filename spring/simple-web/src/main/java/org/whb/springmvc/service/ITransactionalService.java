package org.whb.springmvc.service;

import java.util.List;

import org.whb.springmvc.repository.domain.Article;
import org.whb.springmvc.repository.domain.Author;

public interface ITransactionalService {
    
    public Author getAuthor(Long authorId);
    
    public Article getArticle(long articleId);

    public List<Article> getArticles();
    
    public int saveTransactionSuccess();
    
    public int saveTransactionFail();
}
