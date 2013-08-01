package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class CycleCrawler {

    public static void crawl(final Job job) {
        final CycleContext context = new CycleContext();
        for (final Execution execution : job.getExecutions()) {
            crawl(execution, context);
        }
        if (context.hasCycle()) {
            throw new CycleException(context);
        }
    }

    private static void crawl(final Execution that, final CycleContext context) {
        if (that instanceof Split) {
            crawl((Split)that, context);
        } else if (that instanceof Decision) {
            crawl((Decision)that, context);
        } else if (that instanceof Step) {
            crawl((Step)that, context);
        } else if (that instanceof Flow) {
            crawl((Flow)that, context);
        }
    }

    private static void crawl(final Split that, final CycleContext context) {
        context.addChild(new CycleContext(Split.ELEMENT, that.getId(), that.getNext(), context));
        for (final Flow execution : that.getFlows()) {
            crawl(execution, context);
        }
    }

    private static void crawl(final Decision that, final CycleContext context) {
        context.addChild(new CycleContext(Decision.ELEMENT, that.getId(), null, context));
    }

    private static void crawl(final Step that, final CycleContext context) {
        context.addChild(new CycleContext(Step.ELEMENT, that.getId(), that.getNext(), context));
    }

    private static void crawl(final Flow that, final CycleContext context) {
        context.addChild(new CycleContext(Flow.ELEMENT, that.getId(), that.getNext(), context));
        for (final Execution execution : that.getExecutions()) {
            crawl(execution, context);
        }
    }
}
