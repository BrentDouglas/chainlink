package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.Mergeable;

import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlType
public interface XmlTask<T extends XmlTask<T>> extends Mergeable<T>, Task {
}
