package io.polyaxis.api.utils.constant;

import jakarta.annotation.Nonnull;

/// Database type enumeration.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public enum DatabaseType {

    H2("H2"),
    POSTGRESQL("postgresql"),
    MONGODB("mongodb"),
    REDIS("redis"),
    SQLSERVER("sqlserver"),
    SQLITE("sqlite"),
    CASSANDRA("cassandra"),
    MYSQL("mysql"),
    ORACLE("oracle"),
    DB2("db2"),
    CLICKHOUSE("clickhouse"),
    GREENPLUM("greenplum"),
    DOCUMENTDB("documentdb"),
    DERBY("derby"),
    HIVE("hive"),
    REDSHIFT("redshift"),
    SPARK("spark"),
    TIDB("tidb"),
    DYNAMODB("dynamodb"),
    MIMERSQL("mimersql"),
    PHOENIX("phoenix"),
    BIGQUERY("bigquery"),
    AZURESQL("azuresql"),
    COCKROACHDB("cockroachdb"),
    HSQLDB("hsqldb"),
    // ⇢ vector database
    MARIADB("mariadb"),
    SNOWFLAKE("snowflake"),
    VERTICA("vertica"),
    MILVUS("milvus"),
    CHROMADB("chromadb"),
    PINECONE("pinecone"),
    WEAVIATE("weaviate"),;

    private final String name;

    DatabaseType(String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
