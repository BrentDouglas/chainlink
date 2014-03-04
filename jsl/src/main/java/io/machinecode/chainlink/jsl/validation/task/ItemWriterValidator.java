package io.machinecode.chainlink.jsl.validation.task;

import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterValidator extends PropertyReferenceValidator<ItemWriter> {

    public static final ItemWriterValidator INSTANCE = new ItemWriterValidator();

    protected ItemWriterValidator() {
        super(ItemWriter.ELEMENT);
    }
}
