package com.koushik.fileconverter.config;

import com.koushik.fileconverter.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {

    private final StorageService storageService;

    /**
     * Run cleanup job every hour to remove old files
     * Cron expression: second minute hour day-of-month month day-of-week
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupOldFiles() {
        log.debug("Starting scheduled cleanup of old files");
        storageService.cleanupOldFiles();
        log.debug("Completed scheduled cleanup of old files");
    }

    /**
     * Run cleanup job on application startup after a 1-minute delay
     */
    @Scheduled(initialDelay = 60_000, fixedDelay = Long.MAX_VALUE)
    public void initialCleanup() {
        log.info("Running initial cleanup of old files");
        storageService.cleanupOldFiles();
        log.info("Completed initial cleanup of old files");
    }
}
