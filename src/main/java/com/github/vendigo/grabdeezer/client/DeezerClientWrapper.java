package com.github.vendigo.grabdeezer.client;

import com.github.vendigo.grabdeezer.dto.*;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Slf4j
public class DeezerClientWrapper {

    private static final long MS_TO_SLEEP = 500;
    private final Refill refill = Refill.intervally(49, Duration.ofSeconds(5));
    private final Bandwidth limit = Bandwidth.classic(49, refill);
    private final Bucket bucket = Bucket.builder()
            .addLimit(limit)
            .build();
    private final DeezerClient deezerClient;

    public ArtistDto loadArtist(Long artistId) {
        ArtistDto artistDto = rateLimited(() -> deezerClient.loadArtist(artistId));

        if (artistDto == null) {
            log.warn("Unable to load {} artist", artistId);
        }

        return artistDto;
    }

    public ResultDto<AlbumDto> loadAlbums(Long artistId, Integer pageSize) {
        return rateLimited(() -> deezerClient.loadAlbums(artistId, pageSize));
    }

    public ResultDto<TrackDto> loadTracks(Long albumId) {
        return rateLimited(() -> deezerClient.loadTracks(albumId));
    }

    public TrackDto loadTrack(Long trackId) {
        return rateLimited(() -> deezerClient.loadTrack(trackId));
    }

    public ResultDto<TrackDto> loadTopTracks(Long artistId, Integer pageSize) {
        return rateLimited(() -> deezerClient.loadTopTracks(artistId, pageSize));
    }

    private <T extends ErrorAware> T rateLimited(Supplier<T> action) {
        return rateLimited(action, 1);
    }

    private <T extends ErrorAware> T rateLimited(Supplier<T> action, int multiplier) {
        if (bucket.tryConsume(1)) {
            T result = action.get();
            ErrorDto error = result.error();
            if (error == null) {
                return result;
            } else {
                if (Objects.equals(error.type(), "DataException")) {
                    return null;
                }
                log.debug("Hit error, sleeping x{}", multiplier);
            }
        } else {
            log.debug("Hit rateLimit, sleeping x{}", multiplier);
        }


        sleep(multiplier);
        return rateLimited(action, multiplier + 1);
    }

    @SneakyThrows
    private void sleep(int multiplier) {
        TimeUnit.MILLISECONDS.sleep(multiplier * MS_TO_SLEEP);
    }
}
