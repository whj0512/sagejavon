create table chat_python
(
    id           int auto_increment
        primary key,
    student_id   varchar(255)                 null,
    title        varchar(255)                 null comment '对话主题',
    sort         int                          null,
    long_term    tinyint(1)                   null comment '长期保存标记',
    update_time  datetime                     null,
    deleted_flag tinyint(1) unsigned zerofill null comment '逻辑删除标识'
);

create table exercise_python
(
    id             int auto_increment
        primary key,
    question_text  text          null,
    correct_answer text          null,
    difficulty     int           null,
    choice_a       varchar(1024) null,
    choice_b       varchar(1024) null,
    choice_c       varchar(1024) null,
    choice_d       varchar(1024) null,
    type           int           null comment '0表示代码题，1表示选择题',
    chapter        varchar(255)  null comment '题目所在的章节',
    constraint idx_id
        unique (id)
);

create table exercise_knowledge_python
(
    id           int auto_increment
        primary key,
    knowledge_id int null,
    exercise_id  int null
);

create index idx_exercise_id_python
    on exercise_knowledge_python (exercise_id);

create index idx_exercise_id_knowledge_id_python
    on exercise_knowledge_python (exercise_id, knowledge_id);

create index idx_knowledge_id_python
    on exercise_knowledge_python (knowledge_id);

create table exercise_record_python
(
    id          int auto_increment
        primary key,
    student_id  varchar(50)   null,
    exercise_id int           null,
    answer      text          null,
    score       decimal(3, 2) null,
    submit_time datetime      null,
    suggestion  text          null,
    type        int           null comment '0表示代码题，1表示选择题'
);

create index idx_student_exercise_python
    on exercise_record_python (student_id, exercise_id);

create table history_python
(
    id           int auto_increment
        primary key,
    chat_id      int        null,
    role         tinyint(1) null comment '0用户1AI',
    content      text       null,
    sort         int        null,
    time_stamp   datetime   null on update CURRENT_TIMESTAMP,
    deleted_flag tinyint(1) null comment '逻辑删除标识'
);

create table involved_knowledge_python
(
    id           int auto_increment
        primary key,
    chat_id      int         null,
    knowledge_id int         null,
    name         varchar(50) null
);

create table knowledge_python
(
    id        int auto_increment
        primary key,
    parent_id int          null,
    name      varchar(255) null,
    knowledge varchar(255) null,
    num_q     int          null comment '知识点对应的练习题数量'
);

create index idx_id_python
    on knowledge_python (id);

create table knowledge_edges_python
(
    id           bigint auto_increment
        primary key,
    student_id   varchar(64)                         not null,
    from_node_id varchar(64)                         not null,
    to_node_id   varchar(64)                         not null,
    label        varchar(255)                        null,
    create_time  timestamp default CURRENT_TIMESTAMP null,
    update_time  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table knowledge_nodes_python
(
    id          bigint auto_increment
        primary key,
    student_id  varchar(64)                         not null,
    node_id     varchar(64)                         not null,
    text        varchar(255)                        not null,
    width       int       default 0                 null,
    height      int       default 0                 null,
    create_time timestamp default CURRENT_TIMESTAMP null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table mastery_python
(
    id           int auto_increment
        primary key,
    student_id   varchar(255) null,
    knowledge_id int          null,
    level        tinyint(1)   null comment '4种节点形式',
    query        int          null
);

create table question_keywords_python
(
    id         int auto_increment
        primary key,
    keyword    varchar(255) null,
    prompt_id  int          null,
    teacher_id varchar(255) null
);

create table question_prompt_python
(
    id     int auto_increment
        primary key,
    prompt varchar(255) null
);

create table questions_python
(
    id          int auto_increment
        primary key,
    question    varchar(500) null,
    answer      varchar(500) null,
    keywords_id int          null,
    teacher_id  varchar(255) null
);

create table review_python
(
    id          int auto_increment
        primary key,
    student_id  varchar(255) null,
    exercise_id int          null,
    review      tinyint      null comment '0表示无评价，1表示点赞，-1表示踩'
);

create table tutor_history_python
(
    id         int auto_increment
        primary key,
    student_id varchar(255) null,
    role       tinyint(1)   null,
    content    text         null,
    time_stamp datetime     null
);
