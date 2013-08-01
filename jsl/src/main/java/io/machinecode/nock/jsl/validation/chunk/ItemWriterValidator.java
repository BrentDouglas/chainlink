package io.machinecode.nock.jsl.validation.chunk;

import io.machinecode.nock.jsl.api.chunk.ItemWriter;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterValidator extends PropertyReferenceValidator<ItemWriter> {

    public static final ItemWriterValidator INSTANCE = new ItemWriterValidator();

    protected ItemWriterValidator() {
        super("writer");
    }
}
