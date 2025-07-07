package io.polyaxis.api.utils.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class LoggerScope {
    
    private static final String LOGGER_HEADER = "io.polyaxis.logger.";

    /// `io.polyaxis.logger.network`
    public static final Logger NETWORK = LoggerFactory.getLogger(LOGGER_HEADER + "network");

    /// `io.polyaxis.logger.app.dubbo`
    public static final Logger DUBBO = LoggerFactory.getLogger(LOGGER_HEADER + "app.dubbo");

    /// `io.polyaxis.logger.plugin`
    public static final Logger PLUGIN = LoggerFactory.getLogger(LOGGER_HEADER + "plugin");

    // region ⇢ dubbo AI negotiator
    /// `io.polyaxis.logger.ai.capability`
    public static final Logger AI_CAPABILITY = LoggerFactory.getLogger(LOGGER_HEADER + "ai.capability");

    /// `io.polyaxis.logger.ai.model`
    public static final Logger AI_MODEL = LoggerFactory.getLogger(LOGGER_HEADER + "ai.model");

    /// `io.polyaxis.logger.ai.activity`
    public static final Logger AI_ACTIVITY = LoggerFactory.getLogger(LOGGER_HEADER + "ai.activity");
    // endregion

    // region ⇢ database
    /// `io.polyaxis.logger.db.rdbms`
    public static final Logger RDBMS = LoggerFactory.getLogger(LOGGER_HEADER + "db.rdbms");

    /// `io.polyaxis.logger.db.mongo`
    public static final Logger MONGO = LoggerFactory.getLogger(LOGGER_HEADER + "db.mongo");

    /// `io.polyaxis.logger.db.milvus`
    public static final Logger MILVUS = LoggerFactory.getLogger(LOGGER_HEADER + "db.milvus");

    /// `io.polyaxis.logger.db.redis`
    public static final Logger REDIS = LoggerFactory.getLogger(LOGGER_HEADER + "db.redis");

    /// `io.polyaxis.logger.db.cassandra`
    public static final Logger CASSANDRA = LoggerFactory.getLogger(LOGGER_HEADER + "db.cassandra");

    /// `io.polyaxis.logger.db.elasticsearch`
    public static final Logger ELASTICSEARCH = LoggerFactory.getLogger(LOGGER_HEADER + "db.elasticsearch");
    // endregion
}
