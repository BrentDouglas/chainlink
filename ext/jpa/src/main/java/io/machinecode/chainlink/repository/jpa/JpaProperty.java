package io.machinecode.chainlink.repository.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "property")
public class JpaProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String value;

    public JpaProperty() {
    }

    public JpaProperty(final JpaProperty builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public JpaProperty(final String name, final String value) {
        this.name = name;
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

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public JpaProperty setName(final String name) {
        this.name = name;
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
