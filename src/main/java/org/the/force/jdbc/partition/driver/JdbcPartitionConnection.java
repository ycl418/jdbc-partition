package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.engine.GeneralEngine;
import org.the.force.jdbc.partition.engine.plan.PhysicSqlPlan;
import org.the.force.jdbc.partition.engine.plan.QueryPlan;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.resource.connection.AbstractConnection;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.sql.SqlPlanManager;
import org.the.force.jdbc.partition.engine.PhysicSqlEngine;
import org.the.force.jdbc.partition.engine.QueryEngine;
import org.the.force.jdbc.partition.engine.plan.SqlPlan;
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

    private final SqlPlanManager sqlPlanManager;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ConnectionAdapter connectionAdapter;

    public JdbcPartitionConnection(LogicDbConfig logicDbConfig, SqlPlanManager sqlPlanManager, ThreadPoolExecutor threadPoolExecutor) {
        this.logicDbConfig = logicDbConfig;
        this.sqlPlanManager = sqlPlanManager;
        this.threadPoolExecutor = threadPoolExecutor;
        this.connectionAdapter = new ConnectionAdapter(logicDbConfig);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SqlPlanManager getSqlPlanManager() {
        return sqlPlanManager;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public ConnectionAdapter getConnectionAdapter() {
        return connectionAdapter;
    }

    public Statement createStatement() throws SQLException {
        return new GeneralEngine(this);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        SqlPlan sqlPlan = getSqlPlanManager().getSqlPlan(sql);
        if (sqlPlan instanceof PhysicSqlPlan) {
            return new PhysicSqlEngine(this, (PhysicSqlPlan) sqlPlan);
        } else if (sqlPlan instanceof QueryPlan) {
            return new QueryEngine(this, (QueryPlan) sqlPlan);
        } else {
            //TODO check null
            return null;
        }

    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        SqlPlan sqlPlan = getSqlPlanManager().getSqlPlan(sql);
        boolean b = autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS;
        if (sqlPlan instanceof PhysicSqlPlan) {
            return new PhysicSqlEngine(this, (PhysicSqlPlan) sqlPlan, b);
        } else {
            if (b) {
                //TODO check null
            } else {
                return new QueryEngine(this, (QueryPlan) sqlPlan);
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
        throw new UnsupportedSqlOperatorException();
    }
}
