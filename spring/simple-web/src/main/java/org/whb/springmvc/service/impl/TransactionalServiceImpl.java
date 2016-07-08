package org.whb.springmvc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whb.springmvc.service.ITransactionalService;

@Service
@Transactional
public class TransactionalServiceImpl implements ITransactionalService {

//    @Autowired
//    private SessionFactory sessionFactory;
//
//    @Transactional(readOnly = true)
//    public String greeting() {
//        return "spring without xml works!";
//    }
//
//    @Transactional(readOnly = true)
//    public Book getBook(Long id) {
//        return (Book) getSession().get(Book.class, id);
//    }
//
//    @Transactional(readOnly = true)
//    public Author getAuthor(Long id) {
//        return (Author) getSession().get(Author.class, id);
//    }
//
//    public Book newBook() {
//        Book book = new Book();
//        book.setTitle("java");
//        getSession().save(book);
//        return book;
//    }
//
//    public Author newAuthor() {
//        Book book = newBook();
//        Author author = new Author();
//        author.setName("septem");
//        author.addBook(book);
//        getSession().save(author);
//        return author;
//    }
//
//    private Session getSession() {
//        return sessionFactory.getCurrentSession();
//    }
}
