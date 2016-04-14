package org.whb.common.mongo;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoBeanUtil;

import com.mongodb.DBCursor;

public class DBCursorAdapter<T extends IMongoBean> implements Iterator<T>, Closeable{

    DBCursor cursor;
    
    Class<T> clazz;

    public DBCursorAdapter(Class<T> clazz, DBCursor cursor) {
        super();
        this.cursor = cursor;
        this.clazz = clazz;
    }

    @Override
    public void close() {
        cursor.close();
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
        return (T)MongoBeanUtil.parse(clazz, (Map<?, ?>)cursor.next());
    }

    @Override
    public void remove() {
        cursor.remove();
    }
    
}
