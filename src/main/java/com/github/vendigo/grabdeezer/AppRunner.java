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

    private static final int FULL_LOAD_ITERATIONS = 100;
    private final ArtistFacade artistFacade;

    @Override
    public void run(ApplicationArguments args) {
        topLoad();
        //fullLoad();
        //preload();
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
        int i = 0;
        boolean moreToLoad = true;

        while (moreToLoad && i++ <= FULL_LOAD_ITERATIONS) {
            moreToLoad = artistFacade.fullLoadArtists();
        }
    }


}
