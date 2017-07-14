package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.engine.GeneralSqlEngine;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecution;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutionFactory;
import org.the.force.jdbc.partition.resource.connection.AbstractConnection;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.mysql.MySqlDdMetaDataImpl;
import org.the.force.jdbc.partition.resource.sql.SqlExecutionPlanManager;
import org.the.force.jdbc.partition.engine.BathAbleSqlEngine;
import org.the.force.jdbc.partition.engine.QuerySqlEngine;
import org.the.force.jdbc.partition.resource.sql.SqlExecutionPlan;
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

    private final SqlExecutionPlanManager sqlExecutionPlanManager;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ConnectionAdapter connectionAdapter;

    public JdbcPartitionConnection(LogicDbConfig logicDbConfig, SqlExecutionPlanManager sqlExecutionPlanManager, ThreadPoolExecutor threadPoolExecutor) {
        this.logicDbConfig = logicDbConfig;
        this.sqlExecutionPlanManager = sqlExecutionPlanManager;
        this.threadPoolExecutor = threadPoolExecutor;
        this.connectionAdapter = new ConnectionAdapter(logicDbConfig);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SqlExecutionPlanManager getSqlExecutionPlanManager() {
        return sqlExecutionPlanManager;
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
        SqlExecutionPlan sqlExecutionPlan = getSqlExecutionPlanManager().getSqlExecutionPlan(sql);
        if (sqlExecutionPlan instanceof BatchAbleSqlExecution) {
            return new BathAbleSqlEngine(this, (BatchAbleSqlExecution) sqlExecutionPlan);
        } else if (sqlExecutionPlan instanceof QueryExecutionFactory) {
            return new QuerySqlEngine(this, (QueryExecutionFactory) sqlExecutionPlan);
        } else {
            //TODO check null
            return null;
        }

    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        SqlExecutionPlan sqlExecutionPlan = getSqlExecutionPlanManager().getSqlExecutionPlan(sql);
        boolean b = autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS;
        if (sqlExecutionPlan instanceof BatchAbleSqlExecution) {
            return new BathAbleSqlEngine(this, (BatchAbleSqlExecution) sqlExecutionPlan, b);
        } else {
            if (b) {
                //TODO check null
            } else {
                return new QuerySqlEngine(this, (QueryExecutionFactory) sqlExecutionPlan);
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
