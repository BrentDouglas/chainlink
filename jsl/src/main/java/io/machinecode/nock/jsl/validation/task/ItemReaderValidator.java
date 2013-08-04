package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.jsl.api.task.ItemReader;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderValidator extends PropertyReferenceValidator<ItemReader> {

    public static final ItemReaderValidator INSTANCE = new ItemReaderValidator();

    protected ItemReaderValidator() {
        super(ItemReader.ELEMENT);
    }
}
