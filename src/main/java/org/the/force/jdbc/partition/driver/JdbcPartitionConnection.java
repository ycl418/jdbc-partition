package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.engine.GeneralSqlEngine;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecutor;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutorFactory;
import org.the.force.jdbc.partition.resource.connection.AbstractConnection;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.mysql.MySqlDdMetaDataImpl;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.jdbc.partition.engine.BathAbleSqlEngine;
import org.the.force.jdbc.partition.engine.QuerySqlEngine;
import org.the.force.jdbc.partition.resource.executor.SqlExecutor;
import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/5/15.
 */
public class JdbcPartitionConnection extends AbstractConnection {

    private final LogicDbConfig logicDbConfig;

    private final SqlExecutorManager sqlExecutorManager;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ConnectionAdapter connectionAdapter;

    public JdbcPartitionConnection(LogicDbConfig logicDbConfig, SqlExecutorManager sqlExecutorManager, ThreadPoolExecutor threadPoolExecutor) {
        this.logicDbConfig = logicDbConfig;
        this.sqlExecutorManager = sqlExecutorManager;
        this.threadPoolExecutor = threadPoolExecutor;
        this.connectionAdapter = new ConnectionAdapter(logicDbConfig);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SqlExecutorManager getSqlExecutorManager() {
        return sqlExecutorManager;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public ConnectionAdapter getConnectionAdapter() {
        return connectionAdapter;
    }

    public Statement createStatement() throws SQLException {
        return new GeneralSqlEngine(this);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        SqlExecutor sqlExecutor = getSqlExecutorManager().getSqlExecutor(sql);
        if (sqlExecutor instanceof BatchAbleSqlExecutor) {
            return new BathAbleSqlEngine(this, (BatchAbleSqlExecutor) sqlExecutor);
        } else if (sqlExecutor instanceof QueryExecutorFactory) {
            return new QuerySqlEngine(this, (QueryExecutorFactory) sqlExecutor);
        } else {
            //TODO check null
            return null;
        }

    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        SqlExecutor sqlExecutor = getSqlExecutorManager().getSqlExecutor(sql);
        boolean b = autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS;
        if (sqlExecutor instanceof BatchAbleSqlExecutor) {
            return new BathAbleSqlEngine(this, (BatchAbleSqlExecutor) sqlExecutor, b);
        } else {
            if (b) {
                //TODO check null
            } else {
                return new QuerySqlEngine(this, (QueryExecutorFactory) sqlExecutor);
            }
            //TODO check null
            return null;
        }
    }

    public void close() throws SQLException {
        connectionAdapter.closeConnection();
    }

    public boolean isClosed() throws SQLException {
        return connectionAdapter.isClosed();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connectionAdapter.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return connectionAdapter.getAutoCommit();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        connectionAdapter.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return connectionAdapter.isReadOnly();
    }

    public void commit() throws SQLException {
        connectionAdapter.commit();
    }

    public void rollback() throws SQLException {
        connectionAdapter.rollback();
    }



    public void setTransactionIsolation(int level) throws SQLException {
        connectionAdapter.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return connectionAdapter.getTransactionIsolation();
    }



    public String getSchema() throws SQLException {
        return logicDbConfig.getLogicDbName();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return new MySqlDdMetaDataImpl(logicDbConfig, this.connectionAdapter);
    }
}
