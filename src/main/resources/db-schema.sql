create table dez_artist
(
    id               bigint not null
        primary key,
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

alter table dez_artist
    owner to postgres;

create table dez_album
(
    id           bigint not null
        primary key,
    release_date date,
    title        varchar(255),
    artist_id    bigint
        constraint fkk958jrdw5yo5rpskvu08y9rf
            references dez_artist
);

alter table dez_album
    owner to postgres;

create table dez_artist_queue
(
    artist_id bigint not null
        primary key
);

alter table dez_artist_queue
    owner to postgres;

create table dez_track
(
    id            bigint not null
        primary key,
    duration      integer,
    preview       varchar(1000),
    release_date  date,
    title         varchar(1000),
    album_id      bigint
        constraint fkabf8cr8xivxcbdvralsppe3r8
            references dez_album,
    created_date  timestamp default now(),
    primary_track integer
);

alter table dez_track
    owner to postgres;

create table dez_contributor
(
    track_id  bigint not null
        constraint fk8rc08yolbvi6ss01jv6jxh24q
            references dez_track,
    artist_id bigint
);

alter table dez_contributor
    owner to postgres;

