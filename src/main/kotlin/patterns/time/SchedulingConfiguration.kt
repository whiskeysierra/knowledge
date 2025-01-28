package patterns.time

import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Clock

@EnableScheduling
class SchedulingConfiguration {

    @Bean
    fun taskScheduler(clock: Clock): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.clock = clock
        scheduler.poolSize = 5
        return scheduler
    }
}
