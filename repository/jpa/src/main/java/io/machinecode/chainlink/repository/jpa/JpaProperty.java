package io.machinecode.chainlink.repository.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Entity
@Table(name = "property")
public class JpaProperty {
    private long id;
    private String type;
    private String value;

    public JpaProperty() {
    }

    public JpaProperty(final JpaProperty builder) {
        this.type = builder.type;
        this.value = builder.value;
    }

    public JpaProperty(final String type, final String value) {
        this.type = type;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public JpaProperty setId(final long id) {
        this.id = id;
        return this;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public JpaProperty setType(final String type) {
        this.type = type;
        return this;
    }

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public JpaProperty setValue(final String value) {
        this.value = value;
        return this;
    }

    public static List<JpaProperty> copy(final List<JpaProperty> list) {
        final ArrayList<JpaProperty> ret = new ArrayList<JpaProperty>(list.size());
        for (final JpaProperty prop : list) {
            ret.add(new JpaProperty(prop));
        }
        return ret;
    }
}
