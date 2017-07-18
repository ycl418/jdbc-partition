package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.sql.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecutor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUpdateStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/5/18.
 */
public class UpdateExecutor implements BatchAbleSqlExecutor {

    private static Log logger = LogFactory.getLog(UpdateExecutor.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLUpdateStatement sqlUpdateStatement;

    private UpdateDelParser updateDelParser;

    public UpdateExecutor(LogicDbConfig logicDbConfig, SQLUpdateStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.sqlUpdateStatement = sqlStatement;
        prepare();
    }

    public void prepare() throws Exception {
        updateDelParser = new UpdateDelParser(logicDbConfig, new UpdateDelParserAdapter() {
            public SQLExprTableSource getSQLExprTableSource() {
                return (SQLExprTableSource) sqlUpdateStatement.getTableSource();
            }

            public void setTableSource(SQLExprTableSource sqlExprTableSource) {
                sqlUpdateStatement.setTableSource(sqlExprTableSource);
            }

            public SQLStatement getSQLStatement() {
                return sqlUpdateStatement;
            }

            public SQLExpr getCondition() {
                return sqlUpdateStatement.getWhere();
            }

            public PartitionEvent.EventType getEventType() {
                return PartitionEvent.EventType.UPDATE;
            }
        });
    }

    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        updateDelParser.addSqlLine(physicDbExecutor, logicSqlParameterHolder);
    }


    @Override
    public String toString() {
        return "UpdateExecutor{" + "updateDelParser=" + updateDelParser + '}';
    }
}
