package org.the.force.jdbc.partition.engine.evaluator;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 * SQLExpr的求值程序
 */
public abstract class AbstractSqlExprEvaluator implements SqlExprEvaluator {

    private SQLExpr originalSqlExpr;

    private List<SQLExpr> middleSqlExprs;

    public AbstractSqlExprEvaluator(SQLExpr originalSqlExpr) {
        this.originalSqlExpr = originalSqlExpr;
        this.setParent(originalSqlExpr.getParent());
    }

    public AbstractSqlExprEvaluator() {

    }


    public SQLExpr getOriginalSqlExpr() {
        return originalSqlExpr;
    }

    public void setFromSQLExpr(SQLExpr fromSQLExpr) {
        if (originalSqlExpr != null && middleSqlExprs == null) {
            
            middleSqlExprs = new ArrayList<>();
        }
        if (originalSqlExpr != null) {
            middleSqlExprs.add(originalSqlExpr);
        }
        this.originalSqlExpr = fromSQLExpr;
    }

    public <T extends SqlExprEvaluator> void gatherExprEvaluator(Class<T> target, ExprGatherConfig exprGatherConfig, List<T> resultList) {
        boolean match = false;
        if (exprGatherConfig.isChildClassMatch()) {
            if (target.isAssignableFrom(this.getClass())) {
                resultList.add((T) this);
                match = true;
            }
        } else {
            if (this.getClass() == target) {
                resultList.add((T) this);
                match = true;
            }
        }
        if (!match) {
            List<SqlExprEvaluator> children = children();
            if (children == null || children.isEmpty()) {
                return;
            }
            for (SqlExprEvaluator child : children) {
                child.gatherExprEvaluator(target, exprGatherConfig, resultList);
            }
        }
    }

    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractSqlExprEvaluator that = (AbstractSqlExprEvaluator) o;
        return originalSqlExpr.equals(that.originalSqlExpr);
    }

    public final int hashCode() {
        return originalSqlExpr.hashCode();
    }

    public SQLExpr clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public final void accept(SQLASTVisitor visitor) {
        originalSqlExpr.accept(visitor);
    }

    public SQLObject getParent() {
        return originalSqlExpr.getParent();
    }

    public void setParent(SQLObject parent) {
        originalSqlExpr.setParent(parent);
    }

    public Map<String, Object> getAttributes() {
        return originalSqlExpr.getAttributes();
    }

    public Object getAttribute(String name) {
        return originalSqlExpr.getAttribute(name);
    }

    public void putAttribute(String name, Object value) {
        originalSqlExpr.putAttribute(name, value);
    }

    public Map<String, Object> getAttributesDirect() {
        return originalSqlExpr.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        originalSqlExpr.addBeforeComment(comment);
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        originalSqlExpr.addBeforeComment(comments);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return originalSqlExpr.getBeforeCommentsDirect();
    }

    @Override
    public void addAfterComment(String comment) {
        originalSqlExpr.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        originalSqlExpr.addAfterComment(comments);
    }

    public List<String> getAfterCommentsDirect() {
        return originalSqlExpr.getAfterCommentsDirect();
    }

    public boolean hasBeforeComment() {
        return originalSqlExpr.hasBeforeComment();
    }

    public boolean hasAfterComment() {
        return originalSqlExpr.hasAfterComment();
    }

    public void output(StringBuffer buf) {
        originalSqlExpr.output(buf);
    }
}
