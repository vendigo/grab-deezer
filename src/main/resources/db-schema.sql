create table dez_artist_queue
(
    id               serial
        constraint dez_artist_queue_pk
            primary key,
    deezer_artist_id integer
);

alter table dez_artist_queue
    owner to postgres;

create unique index dez_artist_queue_artistid_uindex
    on dez_artist_queue (id);

create table dez_artist
(
    name      varchar,
    picture   varchar,
    fans      integer,
    deezer_id integer not null
        constraint dez_artist_pk
            primary key
);

alter table dez_artist
    owner to postgres;

create table dez_album
(
    title        varchar,
    release_date date,
    artist_id    integer
        constraint dez_album_dez_artist_deezer_id_fk
            references dez_artist,
    deezer_id    integer not null
        constraint dez_album_pk
            primary key
);

alter table dez_album
    owner to postgres;

create table dez_track
(
    id           serial
        constraint dez_track_pk
            primary key,
    title        varchar,
    duration     integer,
    preview      varchar,
    release_date date,
    album_id     integer,
    deezer_id    integer
);

alter table dez_track
    owner to postgres;

create table dez_contributor
(
    track_id  integer not null
        constraint dez_contributor_dez_track_id_fk
            references dez_track,
    artist_id integer not null
);

alter table dez_contributor
    owner to postgres;

