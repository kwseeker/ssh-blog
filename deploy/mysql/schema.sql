-- 注意这里并没有建表，因为使用JPA,它可以通过映射层代码自动创建数据表的信息(然而还是需要手动创建数据库的)。
-- 当然也可以自行创建数据表，以方便对数据库进行优化。

DROP DATABASE IF EXISTS ssh_blog;
CREATE DATABASE ssh_blog DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- 将 spring.jpa.show-sql：true 显示sql执行开关打开后，可以看到下面两句（表示自动创建数据表，但是自动创建数据表的数据类型估计不是我们想要的）
-- Hibernate: drop table if exists user
-- Hibernate: create table user (id bigint not null auto_increment, email varchar(255), name varchar(255), primary key (id))
INSERT INTO user (id, username, password, name, email) VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Admin', 'admin@gmail.com');
INSERT INTO user (id, username, password, name, email)  VALUES (2, 'kwseeker', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Arvin Lee', 'xiaohuileee@gmail.com');

INSERT INTO authority (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authority (id, name) VALUES (2, 'ROLE_USER');

INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);

-- 技巧：定义好domain之后，可以让Hibernate自动创建表，然后将sql提取出来，修改里面不太合适的数据类型，达到快速完成部署时需要的sql语句
-- Hibernate: alter table blog drop foreign key FKefps36p8dyf3t7yjmic1v0jcs
-- Hibernate: alter table blog drop foreign key FKpxk2jtysqn41oop7lvxcp6dqq
-- Hibernate: alter table blog_comment drop foreign key FKc4ysudanwhfrrhytio0272sx9
-- Hibernate: alter table blog_comment drop foreign key FKb9cpog8ie2cyapsyyt7gikpbl
-- Hibernate: alter table blog_vote drop foreign key FK4bkj28o189fk8jic0snsjfj2h
-- Hibernate: alter table blog_vote drop foreign key FKaar8kqti49vaol2nw9e42lgxc
-- Hibernate: alter table catalog drop foreign key FKk3mprwb52pe5lfv3l2xpmwj8s
-- Hibernate: alter table comment drop foreign key FK8kcum44fvpupyw6f5baccx25c
-- Hibernate: alter table user_authority drop foreign key FKgvxjs381k6f48d5d2yi11uh89
-- Hibernate: alter table user_authority drop foreign key FKpqlsjpkybgos9w2svcri7j8xy
-- Hibernate: alter table vote drop foreign key FKcsaksoe2iepaj8birrmithwve
-- Hibernate: drop table if exists authority
-- Hibernate: drop table if exists blog
-- Hibernate: drop table if exists blog_comment
-- Hibernate: drop table if exists blog_vote
-- Hibernate: drop table if exists catalog
-- Hibernate: drop table if exists comment
-- Hibernate: drop table if exists user
-- Hibernate: drop table if exists user_authority
-- Hibernate: drop table if exists vote
-- Hibernate: create table authority (id bigint not null auto_increment, name varchar(255) not null, primary key (id))
-- Hibernate: create table blog (id bigint not null auto_increment, comment_size integer, content longtext not null, create_time datetime not null, html_content longtext not null, read_size integer, summary varchar(300) not null, tags varchar(255), title varchar(50) not null, vote_size integer, catalog_id bigint, user_id bigint, primary key (id))
-- Hibernate: create table blog_comment (blog_id bigint not null, comment_id bigint not null)
-- Hibernate: create table blog_vote (blog_id bigint not null, vote_id bigint not null)
-- Hibernate: create table catalog (id bigint not null auto_increment, name varchar(255), user_id bigint, primary key (id))
-- Hibernate: create table comment (id bigint not null auto_increment, content varchar(255), create_time datetime, user_id bigint, primary key (id))
-- Hibernate: create table user (id bigint not null auto_increment, avatar varchar(255), email varchar(255), name varchar(255), password varchar(255), username varchar(255), primary key (id))
-- Hibernate: create table user_authority (user_id bigint not null, authority_id bigint not null)
-- Hibernate: create table vote (id bigint not null auto_increment, timestamp datetime, user_id bigint, primary key (id))
-- Hibernate: alter table blog_comment add constraint UK_fk711og2oqkoc82slgnfws8t0 unique (comment_id)
-- Hibernate: alter table blog_vote add constraint UK_nmdrm82d80oq7pw0ab20pia23 unique (vote_id)
-- Hibernate: alter table blog add constraint FKefps36p8dyf3t7yjmic1v0jcs foreign key (catalog_id) references catalog (id)
-- Hibernate: alter table blog add constraint FKpxk2jtysqn41oop7lvxcp6dqq foreign key (user_id) references user (id)
-- Hibernate: alter table blog_comment add constraint FKc4ysudanwhfrrhytio0272sx9 foreign key (comment_id) references comment (id)
-- Hibernate: alter table blog_comment add constraint FKb9cpog8ie2cyapsyyt7gikpbl foreign key (blog_id) references blog (id)
-- Hibernate: alter table blog_vote add constraint FK4bkj28o189fk8jic0snsjfj2h foreign key (vote_id) references vote (id)
-- Hibernate: alter table blog_vote add constraint FKaar8kqti49vaol2nw9e42lgxc foreign key (blog_id) references blog (id)
-- Hibernate: alter table catalog add constraint FKk3mprwb52pe5lfv3l2xpmwj8s foreign key (user_id) references user (id)
-- Hibernate: alter table comment add constraint FK8kcum44fvpupyw6f5baccx25c foreign key (user_id) references user (id)
-- Hibernate: alter table user_authority add constraint FKgvxjs381k6f48d5d2yi11uh89 foreign key (authority_id) references authority (id)
-- Hibernate: alter table user_authority add constraint FKpqlsjpkybgos9w2svcri7j8xy foreign key (user_id) references user (id)
-- Hibernate: alter table vote add constraint FKcsaksoe2iepaj8birrmithwve foreign key (user_id) references user (id)