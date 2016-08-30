package org.whb.springmvc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whb.springmvc.repository.domain.Article;
import org.whb.springmvc.repository.domain.Author;
import org.whb.springmvc.repository.mapper.ArticleMapper;
import org.whb.springmvc.repository.mapper.AuthorMapper;
import org.whb.springmvc.service.ITransactionalService;

/**
 * 支持事务的的服务，声明在类名处的 @Transactional 会被声明在方法处的 @Transactional 覆盖
 * @author whb
 *
 */
@Service
@Transactional(readOnly = true)
public class TransactionalServiceImpl implements ITransactionalService {
    
    @Autowired
    AuthorMapper authorMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public Author getAuthor(Long authorId) {
        return authorMapper.getAuthor(authorId);
    }
    
    public Article getArticle(long articleId) {
        return sqlSessionTemplate.getMapper(ArticleMapper.class).getArticle(articleId);
    }

    public List<Article> getArticles() {
        // 通过"mapper文件名.语句id"
        return sqlSessionTemplate.selectList("Article.getAllArticles");
    }
    
    public List<Map<String,Object>> getBatchReport() {
        Map<String,Object> params = new HashMap<>();
        params.put("_sys_sep_id", 1);
        params.put("cid", "t10");
        return sqlSessionTemplate.selectList("examples.query_commonst", params);
    }
    
    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public int saveTransactionSuccess() {
        Author author = newAuthor();
        Author existAuthor = authorMapper.getAuthor(author.getId());
        if(existAuthor == null){
            authorMapper.insertAuthor(author);
        }else{
            authorMapper.updateAuthor(author);
        }
        
        ArticleMapper articleMapper = sqlSessionTemplate.getMapper(ArticleMapper.class);
        Article article = newArticle();
        articleMapper.insertArticle(article);
        
        return 1;
    }
    
    @Transactional(readOnly = false, noRollbackFor = NullPointerException.class)
    public int saveTransactionFail() {
        int rs = saveTransactionSuccess();
        if(rs == 1){
            throw new RuntimeException("save transaction failed");
        }
        return 0;
    }

    private Author newAuthor() {
        Author author = new Author();
        author.setId(1);
        author.setName("Tom");
        author.setEmail("tom@bb.com");
        return author;
    }

    private Article newArticle() {
        Article article = new Article();
        article.setAuthorId(1);
        article.setSummary("摘要部分");
        article.setTitle("量子力学");
        return article;
    }
}
