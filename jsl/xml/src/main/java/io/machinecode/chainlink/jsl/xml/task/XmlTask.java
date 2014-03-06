package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.Mergeable;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlType
public interface XmlTask<T extends XmlTask<T>> extends Mergeable<T>, Task {
}
