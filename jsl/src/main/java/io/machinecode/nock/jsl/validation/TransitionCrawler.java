package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.element.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class TransitionCrawler {

    public static void validateTransitions(final Job that) {
        final TransitionContext context = new TransitionContext(Job.ELEMENT, that.getId());
        for (final Execution execution : that.getExecutions()) {
            crawl(execution, context);
        }
        boolean failed = context.hasCycle();
        failed = context.hasInvalidTransfer() || failed;
        if (failed) {
            throw new InvalidTransitionException(context);
        }
    }

    private static void crawl(final Execution that, final TransitionContext parent) {
        if (that instanceof Split) {
            crawl((Split)that, parent);
        } else if (that instanceof Decision) {
            crawl((Decision)that, parent);
        } else if (that instanceof Step) {
            crawl((Step)that, parent);
        } else if (that instanceof Flow) {
            crawl((Flow)that, parent);
        }
    }

    private static void crawl(final Split that, final TransitionContext parent) {
        final TransitionContext context = parent.addChild(new TransitionContext(Split.ELEMENT, that.getId(), that.getNext(), parent));
        for (final Flow execution : that.getFlows()) {
            crawl(execution, context);
        }
    }

    private static void crawl(final Decision that, final TransitionContext parent) {
        final TransitionContext context = parent.addChild(new TransitionContext(Decision.ELEMENT, that.getId(), null, parent));
        for (final Transition transition : that.getTransitions()) {
            if (transition instanceof Next) {
                context.addTransition(Next.ELEMENT, null, ((Next) transition).getTo());
            }
        }
    }

    private static void crawl(final Step<?,?> that, final TransitionContext parent) {
        final TransitionContext context = parent.addChild(new TransitionContext(Step.ELEMENT, that.getId(), that.getNext(), parent));
        for (final Transition transition : that.getTransitions()) {
            if (transition instanceof Next) {
                context.addTransition(Next.ELEMENT, null, ((Next) transition).getTo());
            }
        }
    }

    private static void crawl(final Flow that, final TransitionContext parent) {
        final TransitionContext context = parent.addChild(new TransitionContext(Flow.ELEMENT, that.getId(), that.getNext(), parent));
        for (final Execution execution : that.getExecutions()) {
            crawl(execution, context);
        }
        for (final Transition transition : that.getTransitions()) {
            if (transition instanceof Next) {
                context.addTransition(Next.ELEMENT, null, ((Next) transition).getTo());
            }
        }
    }
}
