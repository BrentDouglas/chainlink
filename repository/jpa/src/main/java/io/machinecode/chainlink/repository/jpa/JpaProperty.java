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
    private String key;
    private String value;

    public JpaProperty() {
    }

    public JpaProperty(final JpaProperty builder) {
        this.key = builder.key;
        this.value = builder.value;
    }

    public JpaProperty(final String key, final String value) {
        this.key = key;
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

    @Column(name = "key", nullable = false)
    public String getKey() {
        return key;
    }

    public JpaProperty setKey(final String key) {
        this.key = key;
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
