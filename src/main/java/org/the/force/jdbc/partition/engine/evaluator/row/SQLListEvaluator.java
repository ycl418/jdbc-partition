package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLListEvaluator extends AbstractSqlExprEvaluator {


    private List<SqlExprEvaluator> itemEvaluators = new ArrayList<>();


    public SQLListEvaluator(LogicDbConfig logicDbConfig, SQLListExpr originalSqlExpr) {
        super(originalSqlExpr);
        itemEvaluators
            .addAll(originalSqlExpr.getItems().stream().map(sqlExpr -> (logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlExpr))).collect(Collectors.toList()));
    }

    public SQLListEvaluator(){

    }

    public Object[] eval(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {
        Object[] array = new Object[itemEvaluators.size()];
        for (int i = 0; i < array.length; i++) {
            SqlExprEvaluator itemFunction = itemEvaluators.get(i);
            array[i] = itemFunction.eval(sqlLineExecRequest, data);
        }
        return array;
    }

    public List<SqlExprEvaluator> getItemEvaluators() {
        return itemEvaluators;
    }


    public List<SqlExprEvaluator> children() {
        List<SqlExprEvaluator> list = new ArrayList<>();
        list.addAll(itemEvaluators);
        return list;
    }

}
