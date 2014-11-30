package javax.batch.api.chunk;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ItemProcessor {

    Object processItem(Object item) throws Exception;
}
