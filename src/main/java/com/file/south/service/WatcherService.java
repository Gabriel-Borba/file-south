package com.file.south.service;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.*;

@Configuration
@Log4j2
@Data
public class WatcherService {

    @Value("${sourceFolder}")
    private String sourceFolder;

    private ProcessFileService processFileService;

	@Autowired
	public WatcherService(ProcessFileService processFileService) {
		this.processFileService = processFileService;
	}

	@Bean
    public boolean startWatch() {
        log.info("Starting to watch folder {}", getSourceFolder());
        try {
            Path path = getSourcePath();
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            callWatchEvent(watchService);
            return true;
        } catch (IOException e) {
            log.error("Fail to start application", e);
        } catch (InterruptedException e) {
            log.error("Fail to read event", e);
            Thread.currentThread().interrupt();
        }
        return false;
    }

    protected Path getSourcePath() {
        return Paths.get(getSourceFolder());
    }

    public void callWatchEvent(WatchService watchService) throws InterruptedException {
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                String fileName = event.context().toString();
                log.info("Event starting: {}. File: {}.", event.kind(), fileName);
                processFileService.processFile(fileName);
            }
            log.info("end event");
            key.reset();
        }
	}
}