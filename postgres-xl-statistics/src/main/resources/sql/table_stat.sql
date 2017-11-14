-- 

SELECT
    db,
    ats.schemaname,
    ats.relname,
    pg_table_size('"' || ats.schemaname || '"."' || ats.relname || '"') AS table_size,
    pg_indexes_size('"' || ats.schemaname || '"."' || ats.relname || '"') AS indexes_size,
    pg_total_relation_size('"' || ats.schemaname || '"."' || ats.relname || '"') AS total_size,
    seq_scan,
    seq_tup_read,
    CASE WHEN idx_scan is null THEN 0 ELSE idx_scan END AS idx_scan,
    CASE WHEN idx_tup_fetch is null THEN 0 ELSE idx_tup_fetch END AS idx_tup_fetch,
    n_tup_ins,
    n_tup_upd,
    n_tup_del,
    n_tup_hot_upd,
    n_live_tup,
    n_dead_tup,
    autovacuum_count,
    autoanalyze_count,
    tups,
    pages,
    otta,
    wastedbytes 
FROM pg_stat_all_tables AS ats 
INNER JOIN (
    SELECT relid, current_database() AS db, schemaname, tablename, reltuples::bigint AS tups, relpages::bigint AS pages, otta,
      CASE WHEN relpages < otta THEN 0 ELSE (bs*(sml.relpages-otta))::bigint END AS wastedbytes
    FROM (
      SELECT
        cc.oid AS relid,
        nn.nspname AS schemaname,
        cc.relname AS tablename,
        COALESCE(cc.reltuples,0) AS reltuples,
        COALESCE(cc.relpages,0) AS relpages,
        COALESCE(bs,0) AS bs,
        COALESCE(CEIL((cc.reltuples*((datahdr+ma-
          (CASE WHEN datahdr%ma=0 THEN ma ELSE datahdr%ma END))+nullhdr2+4))/(bs-20::float)),0)::bigint AS otta
      FROM
         pg_class cc
      JOIN pg_namespace nn ON cc.relnamespace = nn.oid AND nn.nspname <> $$information_schema$$
      LEFT JOIN
      (
        SELECT
          ma,bs,foo.nspname,foo.relname,
          (datawidth+(hdr+ma-(case when hdr%ma=0 THEN ma ELSE hdr%ma END)))::numeric AS datahdr,
          (maxfracsum*(nullhdr+ma-(case when nullhdr%ma=0 THEN ma ELSE nullhdr%ma END))) AS nullhdr2
        FROM (
          SELECT
            ns.nspname, tbl.relname, hdr, ma, bs,
            SUM((1-coalesce(null_frac,0))*coalesce(avg_width, 2048)) AS datawidth,
            MAX(coalesce(null_frac,0)) AS maxfracsum,
            hdr+(
              SELECT 1+count(*)/8
              FROM pg_stats s2
              WHERE null_frac<>0 AND s2.schemaname = ns.nspname AND s2.tablename = tbl.relname
            ) AS nullhdr
          FROM pg_attribute att 
          JOIN pg_class tbl ON att.attrelid = tbl.oid
          JOIN pg_namespace ns ON ns.oid = tbl.relnamespace 
          LEFT JOIN pg_stats s ON s.schemaname=ns.nspname
          AND s.tablename = tbl.relname
          AND s.inherited=false
          AND s.attname=att.attname,
          (
            SELECT
              (SELECT current_setting($$block_size$$)::numeric) AS bs,
                CASE WHEN SUBSTRING(SPLIT_PART(v, $$ $$, 2) FROM $$#"[0-9]+.[0-9]+#"%$$ for $$#$$)
                  IN ($$8.0$$,$$8.1$$,$$8.2$$) THEN 27 ELSE 23 END AS hdr,
              CASE WHEN v ~ $$mingw32$$ OR v ~ $$64-bit$$ THEN 8 ELSE 4 END AS ma
            FROM (SELECT version() AS v) AS foo
          ) AS constants
          WHERE att.attnum > 0 AND tbl.relkind=$$r$$
          GROUP BY 1,2,3,4,5
        ) AS foo
      ) AS rs
      ON cc.relname = rs.relname AND nn.nspname = rs.nspname
    ) AS sml 
    ) AS bts 
ON ats.relid = bts.relid ;


