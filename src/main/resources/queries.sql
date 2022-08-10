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

-- Set low priority
update
    dez_artist
set priority = -80
where nb_albums > 100 or (nb_fans < 100 and nb_albums < 3);

update dez_artist
set priority = -100
where nb_albums > 200 or length(name) > 40 or (nb_fans < 10 and nb_albums < 2);


-- Move not existent collabs to artist_queue
insert into dez_artist_queue
select distinct artist_id
from dez_contributor dc
where not exists(select artist_id from dez_artist da where da.id = dc.artist_id);


-- Collab songs to export
select dt.id, dt.title, dt.duration, dt.preview, dt.release_date, string_agg(da.id::text, '|') as contributors
from dez_track dt
         join dez_contributor dc on dt.id = dc.track_id
         join dez_artist da on dc.artist_id = da.id
group by dt.id, dt.title, dt.duration, dt.preview, dt.release_date
having count(dc.artist_id) > 1;

-- Artists to export
select id, name, picture, nb_fans as fans from dez_artist order by fans desc;


