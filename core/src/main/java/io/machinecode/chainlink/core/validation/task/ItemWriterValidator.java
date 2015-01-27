package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.jsl.task.ItemWriter;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemWriterValidator extends PropertyReferenceValidator<ItemWriter> {

    public static final ItemWriterValidator INSTANCE = new ItemWriterValidator();

    protected ItemWriterValidator() {
        super(ItemWriter.ELEMENT);
    }
}
