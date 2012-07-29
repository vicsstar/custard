# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table posts (
  id                        bigint not null,
  title                     varchar(255) not null,
  content                   varchar(4096) not null,
  user_id                   bigint not null,
  created_on                timestamp not null,
  modified_on               timestamp,
  constraint pk_posts primary key (id))
;

create table users (
  id                        bigint not null,
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  username                  varchar(64) not null,
  password                  varchar(255) not null,
  created_on                timestamp not null,
  modified_on               timestamp,
  constraint pk_users primary key (id))
;

create sequence posts_seq;

create sequence users_seq;

alter table posts add constraint fk_posts_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_posts_user_1 on posts (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists posts;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists posts_seq;

drop sequence if exists users_seq;

