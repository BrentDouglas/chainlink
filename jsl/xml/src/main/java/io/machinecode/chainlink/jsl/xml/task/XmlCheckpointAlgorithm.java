package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "CheckpointAlgorithm", propOrder = {
//        "properties"
//})
public class XmlCheckpointAlgorithm extends XmlPropertyReference<XmlCheckpointAlgorithm> implements CheckpointAlgorithm {

    @Override
    public XmlCheckpointAlgorithm copy() {
        return copy(new XmlCheckpointAlgorithm());
    }
}
