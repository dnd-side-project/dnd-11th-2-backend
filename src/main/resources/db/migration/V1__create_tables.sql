create table member
(
    id            bigint generated by default as identity primary key,
    created_at    timestamp(6) with time zone,
    updated_at    timestamp(6) with time zone,
    nickname      varchar(20)  not null,
    role          varchar(255) not null,
    weight_kg     integer,
    main_badge_id bigint
);

create table badge
(
    id             bigint generated by default as identity primary key,
    required_value integer      not null,
    name           varchar(20)  not null,
    description    varchar(100) not null,
    image_path     varchar(255) not null,
    type           varchar(255) not null
);

create table badge_achievement
(
    id         bigint generated by default as identity primary key,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    badge_id   bigint not null,
    member_id  bigint not null
        constraint fk_badge_achievement_member references member
);

create table level
(
    id              bigint generated by default as identity primary key,
    exp_range_start integer not null,
    exp_range_end   integer not null
);

create table member_level
(
    id         bigint generated by default as identity primary key,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    exp        integer not null,
    level_id   bigint  not null,
    member_id  bigint  not null
        constraint uk_member_level_member unique
        constraint fk_member_level_level references member_level references member
);

create table social_profile
(
    id          bigint generated by default as identity primary key,
    created_at  timestamp(6) with time zone,
    updated_at  timestamp(6) with time zone,
    member_id   bigint       not null,
    oauth_email varchar(255) not null,
    oauth_id    varchar(255) not null,
    social_type varchar(255) not null
);

create table running_emoji
(
    id         bigint generated by default as identity primary key,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    image_path varchar(255) not null
);

create table running_record
(
    id               bigint generated by default as identity primary key,
    created_at       timestamp(6) with time zone,
    updated_at       timestamp(6) with time zone,
    average_pace     double precision            not null,
    calorie          double precision            not null,
    distance         double precision            not null,
    duration_seconds integer                     not null,
    emoji_id         bigint                      not null,
    end_at           timestamp(6) with time zone not null,
    member_id        bigint                      not null
        constraint fk_running_record_member references member,
    start_at         timestamp(6) with time zone not null,
    location         varchar(255)                not null,
    route            geometry(LineString, 4326)
);

create table scale
(
    id         bigint generated by default as identity primary key,
    name       varchar(255) not null,
    size_meter integer      not null
);
