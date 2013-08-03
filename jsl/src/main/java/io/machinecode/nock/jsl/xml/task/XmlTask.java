package io.machinecode.nock.jsl.xml.task;

import io.machinecode.nock.jsl.api.task.Task;
import io.machinecode.nock.jsl.xml.util.Mergeable;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlType
public interface XmlTask<T extends XmlTask<T>> extends Mergeable<T>, Task {
}
