package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.driver.result.QueryResult;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.SqlExecutor;
import org.the.force.jdbc.partition.engine.executor.ast.BatchExecutableAst;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecDbNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlUpdateCommand;
import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlLineParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.SqlExecResource;
import org.the.force.jdbc.partition.resource.executor.SqlKey;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuji on 2017/7/29.
 * 单独的一条sql语句 且无无分号分割
 */
public class ParamLineStmt implements ParametricStmt, SqlLine {

    private static Log log = LogFactory.getLog(ParamLineStmt.class);

    private final SqlKey sqlKey;

    private final int paramSize;

    private int lineNumber;
    /**
     * 一行参数
     */
    private SqlLineParameter currentSqlLineParameter;

    /**
     * 批量缓存的sql参数
     */
    private List<SqlLineParameter> batchSqlLineParameters;

    public ParamLineStmt(SqlKey sqlKey, int paramSize) {
        this.sqlKey = sqlKey;
        this.paramSize = paramSize;
        //行号从0开始计数
        currentSqlLineParameter = new SqlLineParameter(0, paramSize);
    }

    public int getParamSize() {
        return paramSize;
    }

    public void addBatch() throws SQLException {
        if (paramSize < 1) {
            throw new RuntimeException("xxxxxx");
        }
        if (batchSqlLineParameters == null) {
            batchSqlLineParameters = new ArrayList<>();
        }
        batchSqlLineParameters.add(currentSqlLineParameter);
        currentSqlLineParameter = new SqlLineParameter(batchSqlLineParameters.size() - 1, paramSize);
    }

    public void clearBatch() throws SQLException {
        currentSqlLineParameter.setLineNumber(0);
        if (batchSqlLineParameters != null) {
            batchSqlLineParameters.clear();
        }
    }

    public void setParameter(int parameterIndex, SqlParameter parameter) {
        if (getParamSize() < 1) {
            throw new RuntimeException("xxxxxx");
        }
        currentSqlLineParameter.setParameter(parameterIndex, parameter);
    }

    public void clearParameters() throws SQLException {
        if (currentSqlLineParameter != null) {
            currentSqlLineParameter.clearParameters();
        }
    }

    public SqlParameter getSqlParameter(int index) {
        if (getParamSize() < 1) {
            throw new RuntimeException("xxxxxx");
        }
        return currentSqlLineParameter.getSqlParameter(index);
    }


    public int getBatchSize() {
        return batchSqlLineParameters == null ? 0 : batchSqlLineParameters.size();
    }


    public String getSql() {
        return sqlKey.getSql();
    }

    public SqlKey getSqlKey() {
        return sqlKey;
    }

    public boolean isBatch() {
        return batchSqlLineParameters != null && !batchSqlLineParameters.isEmpty();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public PResult execute(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig) throws SQLException {
        try {
            SqlExecutor sqlExecutor = sqlExecResource.getSqlExecutorManager().getSqlExecutor(sqlKey);
            if (sqlExecutor instanceof QueryExecutor) {
                QueryExecutor queryExecutor = (QueryExecutor) sqlExecutor;
                log.info("sql查询执行器：\n" + queryExecutor);
                SqlLineExecRequest sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, currentSqlLineParameter);
                ResultSet rs = queryExecutor.execute(sqlLineExecRequest);
                return new QueryResult(rs);
            } else {
                BatchExecutableAst batchExecutableAst = (BatchExecutableAst) sqlExecutor;
                SqlExecDbNode sqlExecDbNode = new SqlExecDbNode();
                UpdateMerger updateMerger;
                if (isBatch()) {
                    Iterator<SqlLineParameter> iterator = batchSqlLineParameters.iterator();
                    int lineTotal = 0;
                    while (iterator.hasNext()) {
                        SqlLineParameter sqlLineParameter = iterator.next();
                        SqlLineExecRequest sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, sqlLineParameter);
                        batchExecutableAst.addExecPhysicNode(sqlExecDbNode, sqlLineExecRequest);
                        lineTotal++;
                        iterator.remove();
                    }
                    updateMerger = new UpdateMerger(lineTotal);
                } else {
                    SqlLineExecRequest sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, currentSqlLineParameter);
                    updateMerger = new UpdateMerger(1);
                    batchExecutableAst.addExecPhysicNode(sqlExecDbNode, sqlLineExecRequest);
                    log.debug("sql解析结果：\n" + sqlExecDbNode.toString());
                }
                SqlUpdateCommand sqlUpdateCommand = new SqlUpdateCommand(sqlExecResource, logicStmtConfig, sqlExecDbNode, updateMerger);
                sqlUpdateCommand.execute();
                return sqlUpdateCommand.getPResult();
            }
        } finally {
            clearBatch();
            clearParameters();
        }

    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        sb.append(lineNumber).append("(").append(paramSize).append("):").append(sqlKey.getSql());
    }
}
