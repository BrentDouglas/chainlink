package io.machinecode.nock.jsl.api.type;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Split extends Type {

    String getNext();

    List<Flow> getFlows();
}
