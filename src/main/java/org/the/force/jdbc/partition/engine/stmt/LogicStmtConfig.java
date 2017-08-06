package org.the.force.jdbc.partition.engine.stmt;

import java.sql.ResultSet;

/**
 * Created by xuji on 2017/7/30.
 */
public class LogicStmtConfig {

    /**
     * @see java.sql.Statement#RETURN_GENERATED_KEYS
     * @see java.sql.Statement#NO_GENERATED_KEYS
     */
    private int autoGeneratedKeys;

    private int[] generatedKeysColumnIndexes;

    private String[] generatedKeysColumnNames;

    private int fetchDirection = ResultSet.FETCH_FORWARD;

    private int fetchSize = 500;

    /**
     * 结果集读取遍历的方式
     *
     * @see java.sql.ResultSet#TYPE_FORWARD_ONLY
     * @see java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE  可滚动但是对数据更新不敏感
     * @see java.sql.ResultSet#TYPE_SCROLL_SENSITIVE    可以滚动游标并且对数据更新敏感（不可重复读？）
     */
    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    /**
     * 是否可以通过结果集直接更新数据
     *
     * @see java.sql.ResultSet#CONCUR_READ_ONLY
     * @see java.sql.ResultSet#CONCUR_UPDATABLE
     */
    private int resultSetConcurrency = ResultSet.CLOSE_CURSORS_AT_COMMIT;

    /**
     * 结果集关闭的方式
     * ResultSet.HOLD_CURSORS_OVER_COMMIT
     * ResultSet.CLOSE_CURSORS_AT_COMMIT
     */
    private int resultSetHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;


    private  int maxFieldSize;

    private int maxRows;

    private int queryTimeOut;//seconds

    public LogicStmtConfig() {
    }

    public LogicStmtConfig(int autoGeneratedKeys) {
        this.autoGeneratedKeys = autoGeneratedKeys;
    }

    public LogicStmtConfig(int[] generatedKeysColumnIndexes) {
        this.generatedKeysColumnIndexes = generatedKeysColumnIndexes;
    }

    public LogicStmtConfig(String[] generatedKeysColumnNames) {
        this.generatedKeysColumnNames = generatedKeysColumnNames;
    }

    public int getAutoGeneratedKeys() {
        return autoGeneratedKeys;
    }

    public void setAutoGeneratedKeys(int autoGeneratedKeys) {
        this.autoGeneratedKeys = autoGeneratedKeys;
    }

    public int[] getGeneratedKeysColumnIndexes() {
        return generatedKeysColumnIndexes;
    }

    public void setGeneratedKeysColumnIndexes(int[] generatedKeysColumnIndexes) {
        this.generatedKeysColumnIndexes = generatedKeysColumnIndexes;
    }

    public String[] getGeneratedKeysColumnNames() {
        return generatedKeysColumnNames;
    }

    public void setGeneratedKeysColumnNames(String[] generatedKeysColumnNames) {
        this.generatedKeysColumnNames = generatedKeysColumnNames;
    }

    public int getFetchDirection() {
        return fetchDirection;
    }

    public void setFetchDirection(int fetchDirection) {
        this.fetchDirection = fetchDirection;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getResultSetType() {
        return resultSetType;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public int getResultSetConcurrency() {
        return resultSetConcurrency;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public int getResultSetHoldability() {
        return resultSetHoldability;
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        this.resultSetHoldability = resultSetHoldability;
    }

    public int getMaxFieldSize() {
        return maxFieldSize;
    }

    public void setMaxFieldSize(int maxFieldSize) {
        this.maxFieldSize = maxFieldSize;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getQueryTimeOut() {
        return queryTimeOut;
    }

    public void setQueryTimeOut(int seconds) {
        this.queryTimeOut = seconds;
    }
}