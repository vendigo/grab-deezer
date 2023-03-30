package com.github.vendigo.grabdeezer;

import com.github.vendigo.grabdeezer.service.ArtistFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.github.vendigo.grabdeezer.service.ArtistFacade.PRELOAD_CHUNK_SIZE;

@Component
@Slf4j
@AllArgsConstructor
public class AppRunner implements ApplicationRunner {
    private final ArtistFacade artistFacade;

    @Override
    public void run(ApplicationArguments args) {
        //loadChart();
        //preload();
        //topLoad();
        //fullLoad();
        loadUpdates();
    }

    private void preload() {
        long artistsToPreload = 1;
        while (artistsToPreload > 0) {
            artistsToPreload = artistFacade.preloadArtists();
            log.info("Preloaded {} artists, {} to go", PRELOAD_CHUNK_SIZE, artistsToPreload);
        }
    }

    private void topLoad() {
        long artistsToLoad = 1;
        while (artistsToLoad > 0) {
            artistsToLoad = artistFacade.topLoadArtists(true);
        }
    }

    private void fullLoad() {
        while (artistFacade.fullLoadArtists()) {
        }
    }

    private void loadUpdates() {
        while (artistFacade.loadUpdates()) {
        }
    }

    private void loadChart() {
        for (int page = 0; page < 3; page++) {
            artistFacade.loadChartArtists(page);
        }
    }

}
