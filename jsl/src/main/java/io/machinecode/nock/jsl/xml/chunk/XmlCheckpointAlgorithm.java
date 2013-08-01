package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.api.chunk.CheckpointAlgorithm;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlCheckpointAlgorithm extends XmlPropertyReference<XmlCheckpointAlgorithm> implements CheckpointAlgorithm {

    @Override
    public XmlCheckpointAlgorithm copy() {
        return copy(new XmlCheckpointAlgorithm());
    }
}
