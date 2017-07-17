package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.executor.factory.BlockQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.executor.factory.UnionQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/4.
 */
public class SubQueriedTableSource extends SQLSubqueryTableSource implements  ExecutableTableSource{
    private final LogicDbConfig logicDbConfig;
    private final SQLSubqueryTableSource subQueryTableSource;
    private final ConditionalSqlTable sqlTable;
    private final QueryExecutor queryExecutor;

    //子查询预期的sqlTable
    public SubQueriedTableSource(LogicDbConfig logicDbConfig, QueryReferFilter queryReferFilter) {
        this.logicDbConfig = logicDbConfig;
        this.sqlTable = queryReferFilter.getReferTable();
        this.subQueryTableSource = (SQLSubqueryTableSource)sqlTable.getSQLTableSource();
        this.setParent(subQueryTableSource.getParent());
        SQLSelectQuery sqlSelectQuery = subQueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelectQuery == null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            queryExecutor = new BlockQueryExecutorFactory(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery, queryReferFilter).getQueryExecutor();
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            queryExecutor = new UnionQueryExecutorFactory(logicDbConfig, (SQLUnionQuery) sqlSelectQuery, queryReferFilter).getQueryExecutor();
        } else {
            throw new ParserException("un supported executor sqlelements:" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()));
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            ((PartitionSqlASTVisitor) visitor).visit(this);
        } else {
            subQueryTableSource.accept(visitor);
        }
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SQLSubqueryTableSource getSubQueryTableSource() {
        return subQueryTableSource;
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }


    @Override
    public String getAlias() {
        return subQueryTableSource.getAlias();
    }

    @Override
    public void setAlias(String alias) {
       // subQueryTableSource.setAlias(alias);
    }

    @Override
    public SQLSelect getSelect() {
        return subQueryTableSource.getSelect();
    }

    @Override
    public void setSelect(SQLSelect select) {
        //subQueryTableSource.setSelect(select);
    }

    @Override
    public int getHintsSize() {
        return subQueryTableSource.getHintsSize();
    }

    @Override
    public List<SQLHint> getHints() {
        return subQueryTableSource.getHints();
    }

    @Override
    public void setHints(List<SQLHint> hints) {
        //subQueryTableSource.setHints(hints);
    }

    @Override
    public void output(StringBuffer buf) {
        subQueryTableSource.output(buf);
    }

    @Override
    public String toString() {
        return subQueryTableSource.toString();
    }

    @Override
    public String computeAlias() {
        return subQueryTableSource.computeAlias();
    }

    @Override
    public SQLSubqueryTableSource clone() {
        return subQueryTableSource.clone();
    }

    @Override
    public SQLExpr getFlashback() {
        return subQueryTableSource.getFlashback();
    }

    @Override
    public void setFlashback(SQLExpr flashback) {
        //subQueryTableSource.setFlashback(flashback);
    }

    @Override
    public SQLObject getParent() {
        return subQueryTableSource.getParent();
    }

    @Override
    public void setParent(SQLObject parent) {
        //subQueryTableSource.setParent(parent);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return subQueryTableSource.getAttributes();
    }

    @Override
    public Object getAttribute(String name) {
        return subQueryTableSource.getAttribute(name);
    }

    @Override
    public void putAttribute(String name, Object value) {
        subQueryTableSource.putAttribute(name, value);
    }

    @Override
    public Map<String, Object> getAttributesDirect() {
        return subQueryTableSource.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        subQueryTableSource.addBeforeComment(comment);
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        subQueryTableSource.addBeforeComment(comments);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return subQueryTableSource.getBeforeCommentsDirect();
    }

    @Override
    public void addAfterComment(String comment) {
        subQueryTableSource.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        subQueryTableSource.addAfterComment(comments);
    }

    @Override
    public List<String> getAfterCommentsDirect() {
        return subQueryTableSource.getAfterCommentsDirect();
    }

    @Override
    public boolean hasBeforeComment() {
        return subQueryTableSource.hasBeforeComment();
    }

    @Override
    public boolean hasAfterComment() {
        return subQueryTableSource.hasAfterComment();
    }
}
