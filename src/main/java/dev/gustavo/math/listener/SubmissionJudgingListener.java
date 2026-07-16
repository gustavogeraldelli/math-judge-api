package dev.gustavo.math.listener;

import dev.gustavo.math.event.SubmissionCreatedEvent;
import dev.gustavo.math.processor.SubmissionJudgingProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionJudgingListener {

    private final SubmissionJudgingProcessor submissionJudgingProcessor;

    @Async("submissionJudgingExecutor")
    @EventListener
    public void handle(SubmissionCreatedEvent event) {
        submissionJudgingProcessor.judge(event.submissionId());
    }
}
