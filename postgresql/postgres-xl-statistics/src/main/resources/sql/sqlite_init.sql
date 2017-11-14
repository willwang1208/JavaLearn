-- TPS
CREATE TABLE stat_statements_tps(
    node         TEXT     NOT NULL,
    dt           DATE     NOT NULL,
    tps          INT      NOT NULL
);

-- TOP SQL
CREATE TABLE snap_pg_stat_statements(
    node                  TEXT      NOT NULL,
    dt                    DATE      NOT NULL,
    rolname               TEXT      NOT NULL,
    dbname                TEXT      NOT NULL,
    sql                   TEXT      NOT NULL,
    call_count            BIGINT    NOT NULL,
    total_time            REAL      NOT NULL,
    min_time              REAL      NOT NULL,
    max_time              REAL      NOT NULL,
    mean_time             REAL      NOT NULL,
    stddev_time           REAL      NOT NULL,
    row_count             BIGINT    NOT NULL,
    shared_blks_hit       BIGINT    NOT NULL,
    shared_blks_read      BIGINT    NOT NULL,
    shared_blks_dirtied   BIGINT    NOT NULL,
    shared_blks_written   BIGINT    NOT NULL,
    local_blks_hit        BIGINT    NOT NULL,
    local_blks_read       BIGINT    NOT NULL,
    local_blks_dirtied    BIGINT    NOT NULL,
    local_blks_written    BIGINT    NOT NULL,
    temp_blks_read        BIGINT    NOT NULL,
    temp_blks_written     BIGINT    NOT NULL,
    blk_read_time         REAL      NOT NULL,
    blk_write_time        REAL      NOT NULL
);

-- TABLE STAT
CREATE TABLE stat_table(
    node                  TEXT      NOT NULL,
    dt                    DATE      NOT NULL,
    db                    TEXT      NOT NULL,
    schemaname            TEXT      NOT NULL,
    tablename             TEXT      NOT NULL,
    table_size            BIGINT    NOT NULL,
    indexes_size          BIGINT    NOT NULL,
    total_size            BIGINT    NOT NULL,
    seq_scan              BIGINT    NOT NULL,
    seq_tup_read          BIGINT    NOT NULL,
    idx_scan              BIGINT    NOT NULL,
    idx_tup_fetch         BIGINT    NOT NULL,
    n_tup_ins             BIGINT    NOT NULL,
    n_tup_upd             BIGINT    NOT NULL,
    n_tup_del             BIGINT    NOT NULL,
    n_tup_hot_upd         BIGINT    NOT NULL,
    tups                  BIGINT    NOT NULL,
    n_live_tup            BIGINT    NOT NULL,
    n_dead_tup            BIGINT    NOT NULL,
    pages                 BIGINT    NOT NULL,
    otta                  BIGINT    NOT NULL,
    wastedbytes           BIGINT    NOT NULL,
    autovacuum_count      BIGINT    NOT NULL,
    autoanalyze_count     BIGINT    NOT NULL
);

-- INDEX STAT
CREATE TABLE stat_index(
    node                  TEXT      NOT NULL,
    dt                    DATE      NOT NULL,
    db                    TEXT      NOT NULL,
    schemaname            TEXT      NOT NULL,
    tablename             TEXT      NOT NULL,
    idxname               TEXT      NOT NULL,
    idx_size              BIGINT    NOT NULL,
    idx_scan              BIGINT    NOT NULL,
    idx_tup_read          BIGINT    NOT NULL,
    idx_tup_fetch         BIGINT    NOT NULL,
    itups                 BIGINT    NOT NULL,
    ipages                BIGINT    NOT NULL,
    iotta                 BIGINT    NOT NULL,
    iwastedsize           BIGINT    NOT NULL,
    indexdef              TEXT      NOT NULL
);

-- Long time running activity
CREATE TABLE snap_pg_stat_activity(
    node                  TEXT      NOT NULL,
    dt                    DATE      NOT NULL,
    xact_time             REAL      NOT NULL,
    query_time            REAL      NOT NULL,
    sql                   TEXT      NOT NULL,
    state                 TEXT      NOT NULL,
    waiting               TEXT      NOT NULL,
    pid                   TEXT      NOT NULL,
    datname               TEXT      NOT NULL,
    client_addr           TEXT,
    relname               TEXT,
    locktype              TEXT,
    mode                  TEXT,
    granted               TEXT
);

-- DEFAULT CONDITION
CREATE TABLE default_condition(
    name                  TEXT      NOT NULL,
    condition             TEXT      NOT NULL
);


--CREATE TABLE demo( --asdsa
--   ID INT PRIMARY KEY     NOT NULL,
--   NAME           TEXT    NOT NULL,
--   AGE            INT     NOT NULL,  --asdsad
--   ADDRESS        CHAR(50),
--   SALARY         REAL
--);

--CREATE VIEW stat_statements_tpm AS 
--    SELECT strftime('%Y-%m-%d %H:%M', dt) sdt, node, sum(tps)/count(dt)*60 tpm 
--    FROM stat_statements_tps GROUP BY node, sdt;


