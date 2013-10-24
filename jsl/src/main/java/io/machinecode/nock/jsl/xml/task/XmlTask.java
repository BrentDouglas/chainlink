package io.machinecode.nock.jsl.xml.task;

import io.machinecode.nock.spi.element.task.Task;
import io.machinecode.nock.spi.Mergeable;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlType
public interface XmlTask<T extends XmlTask<T>> extends Mergeable<T>, Task {
}
