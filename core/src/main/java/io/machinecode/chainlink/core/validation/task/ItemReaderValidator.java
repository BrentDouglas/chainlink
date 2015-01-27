package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.jsl.task.ItemReader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemReaderValidator extends PropertyReferenceValidator<ItemReader> {

    public static final ItemReaderValidator INSTANCE = new ItemReaderValidator();

    protected ItemReaderValidator() {
        super(ItemReader.ELEMENT);
    }
}
