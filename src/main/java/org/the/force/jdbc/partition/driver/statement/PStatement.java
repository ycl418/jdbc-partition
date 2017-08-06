package org.the.force.jdbc.partition.driver.statement;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.driver.result.BatchResult;
import org.the.force.jdbc.partition.driver.result.UpdateResult;
import org.the.force.jdbc.partition.engine.stmt.LogicStmt;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.stmt.impl.MultiSqlFactory;
import org.the.force.jdbc.partition.engine.stmt.impl.BatchSqlLineStmt;
import org.the.force.jdbc.partition.engine.stmt.impl.ParametricStmt;
import org.the.force.jdbc.partition.resource.statement.AbstractStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * Created by xuji on 2017/7/29.
 */
public class PStatement extends AbstractStatement {


    private static Log log = LogFactory.getLog(PStatement.class);

    protected final JdbcPartitionConnection connection;

    protected final LogicStmtConfig logicStmtConfig;

    protected PResult currentResult;

    /**
     * 当前statment 是否是关闭的
     */
    protected boolean closed;

    private BatchSqlLineStmt multiSqlLineSql = new BatchSqlLineStmt();

    public PStatement(JdbcPartitionConnection connection) {
        this(connection, new LogicStmtConfig());
    }

    public PStatement(JdbcPartitionConnection connection, LogicStmtConfig logicStmtConfig) {
        this.logicStmtConfig = logicStmtConfig;
        this.connection = connection;
    }


    protected void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("No operations allowed after statement closed.");
        }
    }


    //======queue========
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery(MultiSqlFactory.getLogicSql(sql));
    }

    public ResultSet executeQuery(LogicStmt logicStmt) throws SQLException {
        checkClosed();
        ensureResultSetIsEmpty();
        currentResult = connection.executeLogicSql(logicStmt, this.logicStmtConfig);
        return currentResult.getResultSet();
    }

    //===== update =====
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(MultiSqlFactory.getLogicSql(sql), this.logicStmtConfig);
    }


    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(autoGeneratedKeys));
    }


    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(columnIndexes));
    }


    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(columnNames));
    }

    protected int executeUpdate(LogicStmt logicStmt, LogicStmtConfig logicStmtConfig) throws SQLException {
        checkClosed();
        ensureResultSetIsEmpty();
        currentResult = connection.executeLogicSql(logicStmt, logicStmtConfig);
        return currentResult.getUpdateCount();
    }

    // jdbc规范: 返回true表示executeQuery，false表示executeUpdate
    public boolean execute(String sql) throws SQLException {

        return execute(MultiSqlFactory.getLogicSql(sql), logicStmtConfig);
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(autoGeneratedKeys));
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(columnIndexes));
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(MultiSqlFactory.getLogicSql(sql), new LogicStmtConfig(columnNames));
    }

    // jdbc规范: 返回true表示executeQuery，false表示executeUpdate
    protected boolean execute(LogicStmt logicStmt, LogicStmtConfig logicStmtConfig) throws SQLException {
        checkClosed();
        ensureResultSetIsEmpty();
        currentResult = connection.executeLogicSql(logicStmt, logicStmtConfig);
        return currentResult.getUpdateCount() != -1;
    }

    //==============batch api=========

    public void addBatch(String sql) throws SQLException {
        ParametricStmt parametricSql = MultiSqlFactory.getLogicSql(sql);
        if (parametricSql == null) {
            return;
        }
        if (parametricSql.getParamSize() > 0) {
            throw new RuntimeException("multiSqlLineSql paramSize() > 0");
        }
        multiSqlLineSql.addBatch(parametricSql);
    }

    public int[] executeBatch() throws SQLException {
        return executeBatch(multiSqlLineSql, this.logicStmtConfig);
    }

    public int[] executeBatch(LogicStmt logicStmt, LogicStmtConfig logicStmtConfig) throws SQLException {
        checkClosed();
        ensureResultSetIsEmpty();
        currentResult = connection.executeLogicSql(logicStmt, logicStmtConfig);
        if (currentResult instanceof UpdateResult) {
            return ((UpdateResult) currentResult).getUpdateMerger().toArray();
        } else if (currentResult instanceof BatchResult) {
            return ((BatchResult) currentResult).getUpdateCountArray();
        } else {
            return new int[] {currentResult.getUpdateCount()};
        }
    }

    public void clearBatch() throws SQLException {
        multiSqlLineSql.clearBatch();
    }


    public boolean isClosed() throws SQLException {
        return this.closed;
    }


    public void close() throws SQLException {
        close(true);
    }

    void close(boolean removeThis) throws SQLException {
        if (closed) {
            return;
        }
        try {
            if (currentResult != null) {
                currentResult.close();
            }

            if (removeThis) {
                connection.removeStatement(this);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            currentResult = null;
        }
        closed = true;
    }

    protected void ensureResultSetIsEmpty() throws SQLException {
        if (currentResult != null) {
            // log.debug("result set is not null,close current result set");
            try {
                currentResult.close();
            } catch (SQLException e) {
                log.error("exception on close last result set . can do nothing..", e);
            } finally {
                currentResult = null;
            }
        }
    }

    public int getMaxFieldSize() throws SQLException {
        return logicStmtConfig.getMaxFieldSize();
    }

    public void setMaxFieldSize(int max) throws SQLException {
        logicStmtConfig.setMaxFieldSize(max);
    }

    public int getMaxRows() throws SQLException {
        return logicStmtConfig.getMaxRows();
    }

    public void setMaxRows(int max) throws SQLException {
        logicStmtConfig.setMaxRows(max);
    }

    public ResultSet getResultSet() throws SQLException {
        return currentResult.getResultSet();
    }

    public int getUpdateCount() throws SQLException {
        return currentResult.getUpdateCount();
    }

    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public int getQueryTimeout() throws SQLException {
        return this.logicStmtConfig.getQueryTimeOut();
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        this.logicStmtConfig.setQueryTimeOut(seconds);

    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T) this;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass());
    }

    public void setFetchDirection(int direction) throws SQLException {
        logicStmtConfig.setFetchDirection(direction);

    }

    public int getFetchDirection() throws SQLException {
        return logicStmtConfig.getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException {
        logicStmtConfig.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException {
        return logicStmtConfig.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return logicStmtConfig.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return logicStmtConfig.getResultSetType();
    }

    public int getResultSetHoldability() throws SQLException {
        return logicStmtConfig.getResultSetHoldability();
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }


    public void clearWarnings() throws SQLException {
    }

    public void setResultSetType(int resultSetType) {
        logicStmtConfig.setResultSetType(resultSetType);
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        logicStmtConfig.setResultSetConcurrency(resultSetConcurrency);
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        logicStmtConfig.setResultSetHoldability(resultSetHoldability);
    }


    public boolean getMoreResults() throws SQLException {
        return currentResult.getMoreResults();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return currentResult.getMoreResults(current);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return currentResult.getGeneratedKeys();
    }


    public void cancel() throws SQLException {
    }


}
