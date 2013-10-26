package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.element.task.Chunk.CheckpointPolicy;
import io.machinecode.nock.spi.util.Message;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkValidator extends ValidatingVisitor<Chunk> {

    public static final ChunkValidator INSTANCE = new ChunkValidator();

    protected ChunkValidator() {
        super(Chunk.ELEMENT);
    }

    @Override
    public void doVisit(final Chunk that, final VisitorNode context) {
        if (that.getCheckpointPolicy() == null) {
            context.addProblem(Message.attributeRequired("checkpoint-policy"));
        }
        if (!CheckpointPolicy.ITEM.equals(that.getCheckpointPolicy())
                && !CheckpointPolicy.CUSTOM.equals(that.getCheckpointPolicy())) {
            context.addProblem(Message.attributeMatches("checkpoint-policy", that.getCheckpointPolicy(), CheckpointPolicy.ITEM, CheckpointPolicy.CUSTOM));
        }

        //if (that.getItemCount() < 0) {
        //    context.addProblem(Problem.attributePositive("item-count", that.getItemCount()));
        //}
        //if (that.getTimeLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("time-limit", that.getTimeLimit()));
        //}
        //if (that.getSkipLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("skip-limit", that.getSkipLimit()));
        //}
        //if (that.getRetryLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("retry-limit", that.getRetryLimit()));
        //}

        if (that.getReader() == null) {
            context.addProblem(Message.notNullElement("reader"));
        } else {
            ItemReaderValidator.INSTANCE.visit(that.getReader(), context);
        }
        if (that.getWriter() == null) {
            context.addProblem(Message.notNullElement("writer"));
        } else {
            ItemWriterValidator.INSTANCE.visit(that.getWriter(), context);
        }
        if (that.getProcessor() != null) {
            ItemProcessorValidator.INSTANCE.visit(that.getProcessor(), context);
        }

        //Nullable

        if (that.getCheckpointAlgorithm() != null) {
            CheckpointAlgorithmValidator.INSTANCE.visit(that.getCheckpointAlgorithm(), context);
        }
        if (that.getNoRollbackExceptionClasses() != null) {
            ExecutionClassFilterValidator.NO_ROLLBACK.visit(that.getNoRollbackExceptionClasses(), context);
        }
        if (that.getSkippableExceptionClasses() != null) {
            ExecutionClassFilterValidator.SKIPPABLE.visit(that.getSkippableExceptionClasses(), context);
        }
        if (that.getRetryableExceptionClasses() != null) {
            ExecutionClassFilterValidator.RETRYABLE.visit(that.getRetryableExceptionClasses(), context);
        }
    }
}
