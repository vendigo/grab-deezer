-- Top Artists
select *
from dez_artist
order by priority desc, nb_fans desc;

-- Db stats
select count(*)
from dez_artist;

select count(*)
from dez_track;
--966054


-- Collab songs to export
select dt.id, dt.title, dt.duration, dt.preview, dt.release_date, string_agg(da.id::text, '|') as contributors
from dez_track dt
         join dez_contributor dc on dt.id = dc.track_id
         join dez_artist da on dc.artist_id = da.id
group by dt.id, dt.title, dt.duration, dt.preview, dt.release_date
having count(dc.artist_id) > 1;

-- Artists to export
select id, name, picture, nb_fans as fans from dez_artist order by fans desc;


