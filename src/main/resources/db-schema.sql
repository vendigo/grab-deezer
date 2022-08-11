create table dez_artist
(
    id               bigint not null primary key,
    nb_fans          integer,
    name             varchar(1000),
    picture          varchar(1000),
    full_loaded      boolean   default false,
    top_loaded       boolean   default false,
    priority         integer   default 0,
    last_update_time timestamp default now(),
    nb_albums        integer,
    failed_to_load   boolean   default false
);

create table dez_album
(
    id           bigint not null primary key,
    release_date date,
    title        varchar(255),
    artist_id    bigint
        constraint dez_album_dez_artist_fk references dez_artist
);

create table dez_artist_queue
(
    artist_id bigint not null primary key
);

create table dez_track
(
    id           bigint not null primary key,
    duration     integer,
    preview      varchar(1000),
    release_date date,
    title        varchar(1000),
    album_id     bigint
        constraint dez_track_dez_album_fk references dez_album,
    created_date timestamp default now()
);

create table dez_contributor
(
    track_id  bigint not null
        constraint dez_contributor_dez_track_fk references dez_track,
    artist_id bigint
);

alter table dez_contributor owner to postgres;
alter table dez_track owner to postgres;
alter table dez_artist_queue owner to postgres;
alter table dez_album owner to postgres;
alter table dez_artist owner to postgres;