/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.the.force.thirdparty.druid.sql.ast.statement;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatementImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

public class SQLCommitStatement extends SQLStatementImpl {

    // oracle
    private boolean write;
    private Boolean wait;
    private Boolean immediate;

    // mysql
    private boolean work = false;
    private Boolean chain;
    private Boolean release;

    // executor server
    private SQLExpr transactionName;
    private SQLExpr delayedDurability;

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, transactionName);
            acceptChild(visitor, delayedDurability);
        }
        visitor.endVisit(this);
    }

    // oracle
    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public Boolean getWait() {
        return wait;
    }

    public void setWait(Boolean wait) {
        this.wait = wait;
    }

    public Boolean getImmediate() {
        return immediate;
    }

    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
    }

    // mysql
    public Boolean getChain() {
        return chain;
    }

    public void setChain(Boolean chain) {
        this.chain = chain;
    }

    public Boolean getRelease() {
        return release;
    }

    public void setRelease(Boolean release) {
        this.release = release;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    // executor server
    public SQLExpr getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(SQLExpr transactionName) {
        if (transactionName != null) {
            transactionName.setParent(this);
        }
        this.transactionName = transactionName;
    }

    public SQLExpr getDelayedDurability() {
        return delayedDurability;
    }

    public void setDelayedDurability(SQLExpr delayedDurability) {
        if (delayedDurability != null) {
            delayedDurability.setParent(this);
        }
        this.delayedDurability = delayedDurability;
    }
}
