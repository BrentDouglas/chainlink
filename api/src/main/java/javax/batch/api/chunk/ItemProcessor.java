package javax.batch.api.chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemProcessor {

    Object processItem(Object item) throws Exception;
}
