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
package org.the.force.thirdparty.druid.sql.ast.expr;

import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

public class SQLNCharExpr extends SQLTextLiteralExpr {

    public SQLNCharExpr(){

    }

    public SQLNCharExpr(String text){
        super(text);
    }

    public void output(StringBuffer buf) {
        if ((this.text == null) || (this.text.length() == 0)) {
            buf.append("NULL");
            return;
        }

        buf.append("N'");
        buf.append(this.text.replaceAll("'", "''"));
        buf.append("'");
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public SQLNCharExpr clone() {
        return new SQLNCharExpr(text);
    }
}