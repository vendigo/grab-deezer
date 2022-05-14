package com.github.vendigo.grabdeezer.client;

import com.github.vendigo.grabdeezer.dto.AlbumDto;
import com.github.vendigo.grabdeezer.dto.ArtistDto;
import com.github.vendigo.grabdeezer.dto.ResultDto;
import com.github.vendigo.grabdeezer.dto.TrackDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "deezer", url = "https://api.deezer.com")
public interface DeezerClient {

    @GetMapping("artist/{artistId}")
    ArtistDto loadArtist(@PathVariable("artistId") Long artistId);

    @GetMapping("artist/{artistId}/albums")
    ResultDto<AlbumDto> loadAlbums(@PathVariable("artistId") Long artistId, @RequestParam("limit") Integer limit);

    @GetMapping("album/{albumId}/tracks")
    ResultDto<TrackDto> loadTracks(@PathVariable("albumId") Long albumId);

    @GetMapping("track/{trackId}")
    TrackDto loadTrack(@PathVariable("trackId") Long trackId);

    @GetMapping("artist/{artistId}/related")
    ResultDto<ArtistDto> loadRelatedArtists(@PathVariable("artistId") Long artistId);

    @GetMapping("artist/{artistId}/top")
    ResultDto<TrackDto> loadTopTracks(@PathVariable("artistId") Long artistId, @RequestParam("limit") Integer limit);
}
