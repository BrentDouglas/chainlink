package io.machinecode.chainlink.repository.mongo;

import com.mongodb.DBObject;
import org.jongo.ResultHandler;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
class Handler<T> implements ResultHandler<T> {

    private final String field;

    Handler(final String field) {
        this.field = field;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T map(final DBObject result) {
        return (T) result.get(field);
    }
}
