-- Top Artists
select *
from dez_artist
order by priority desc, nb_fans desc;

-- Db stats
select count(*)
from dez_artist;
-- 126641

select count(*)
from dez_track;
-- 1220733

-- Set low priority
update
    dez_artist
set priority = -80
where nb_albums > 100
   or (nb_fans < 100 and nb_albums < 3);

update dez_artist
set priority = -100
where nb_albums > 200
   or length(name) > 40
   or (nb_fans < 10 and nb_albums < 2);

-- Move not existent collabs to artist_queue
insert into dez_artist_queue
select distinct artist_id
from dez_contributor dc
where not exists(select artist_id from dez_artist da where da.id = dc.artist_id);

-- Artists to export
select id, name, picture, nb_fans as fans, priority
from dez_artist
order by fans desc;

-- Collab songs to export
select dt.id,
                   dt.title,
                   dt.duration,
                   dt.preview,
                   dt.release_date,
                   string_agg(da.id::text, '|') as contributors
            from dez_track dt
                     join dez_contributor dc on dt.id = dc.track_id
                     join dez_artist da on dc.artist_id = da.id
            where dt.primary_track = 1
            group by dt.id, dt.title, dt.duration, dt.preview, dt.release_date
            having count(dc.artist_id) > 1;

-- Smaller db (To fit into Aurora free tier)
-- Artists
select id, name, picture, nb_fans as fans, priority
from dez_artist
where priority > -90
order by priority desc, fans desc;

-- Tracks
select dt.id,
       dt.title,
       dt.duration,
       dt.preview,
       dt.release_date,
       string_agg(da.id::text, '|') as contributors
from dez_track dt
         join dez_contributor dc on dt.id = dc.track_id
         join dez_artist da on dc.artist_id = da.id
where dt.primary_track = 1
group by dt.id, dt.title, dt.duration, dt.preview, dt.release_date
having count(dc.artist_id) > 1;

-- Set primary tracks
update dez_track set primary_track = 1 where id in (
select distinct trackId  from (
select dt.id as trackId, a1.id, a2.id, rank() over (partition by a1.id, a2.id order by dt.id desc) as trackRank
from dez_artist a1
         join dez_contributor dc1 on a1.id = dc1.artist_id
         join dez_track dt on dc1.track_id = dt.id
         join dez_contributor dc2 on dc2.track_id = dt.id
         join dez_artist a2 on dc2.artist_id = a2.id
where a1.id > a2.id and a1.priority > -90 and a2.priority > -90
order by a1.id, a2.id) as ranks
where trackRank = 1);

--Track updates
select dt.id,
       dt.title,
       dt.duration,
       dt.preview,
       dt.release_date,
       string_agg(da.id::text, '|') as contributors
from dez_track dt
         join dez_contributor dc on dt.id = dc.track_id
         join dez_artist da on dc.artist_id = da.id
where created_date > DATE'2022-12-01'
group by dt.id, dt.title, dt.duration, dt.preview, dt.release_date
having count(dc.artist_id) > 1;

-- Artist updates
select id, name, picture, nb_fans as fans, priority
from dez_artist
where priority > 0
order by priority desc, fans desc;


