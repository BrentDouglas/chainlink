package io.machinecode.nock.jsl.visitor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
final class Transition {
    Transition(final String element, final String id, final String next) {
        this.element = element;
        this.id = id;
        this.next = next;
    }

    final String element;
    final String id;
    final String next;
}
