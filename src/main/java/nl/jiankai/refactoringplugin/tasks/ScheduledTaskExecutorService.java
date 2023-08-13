package nl.jiankai.refactoringplugin.tasks;

import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A service that allows you to schedule the execution of tasks
 */
public class ScheduledTaskExecutorService<T> implements TaskExecutorService<ScheduledTask<T>, T> {
    private static final Logger LOGGER = Logger.getLogger(ScheduledTaskExecutorService.class.getName());
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    @Override
    public Future<T> executeTask(ScheduledTask<T> task) {
        LOGGER.info("Scheduling non-recurring task");
        return executorService.schedule(task.getTask(), task.delay(), TimeUnit.SECONDS);
    }

    public Future<?> executeRecurringTask(ScheduledTask<T> task) {

        if (task.isRecurring()) {
            LOGGER.info("Scheduling recurring task with period of %s %s with a delay of %s seconds".formatted(task.period(), task.periodTimeUnit().toString(), task.delay()));
            return executorService.scheduleWithFixedDelay(() -> {
                try {
                    task.getTask().call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, task.period(), task.delay(), TimeUnit.SECONDS);
        } else {
            throw new IllegalArgumentException("A non recurring task was provided to a recurring task call");
        }
    }
}
