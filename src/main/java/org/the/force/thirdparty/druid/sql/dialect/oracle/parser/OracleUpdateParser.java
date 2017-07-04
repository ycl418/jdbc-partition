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
package org.the.force.thirdparty.druid.sql.dialect.oracle.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.the.force.thirdparty.druid.sql.parser.Lexer;
import org.the.force.thirdparty.druid.sql.parser.SQLStatementParser;
import org.the.force.thirdparty.druid.sql.parser.Token;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

public class OracleUpdateParser extends SQLStatementParser {

    public OracleUpdateParser(String sql) {
        super(new OracleExprParser(sql));
    }

    public OracleUpdateParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    public OracleUpdateStatement parseUpdateStatement() {
        OracleUpdateStatement update = new OracleUpdateStatement();
        
        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            parseHints(update);

            if (identifierEquals("ONLY")) {
                update.setOnly(true);
            }

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            update.setTableSource(tableSource);

            if ((update.getAlias() == null) || (update.getAlias().length() == 0)) {
                update.setAlias(tableAlias());
            }
        }

        parseUpdateSet(update);

        parseWhere(update);

        parseReturn(update);

        parseErrorLoging(update);

        return update;
    }

    private void parseErrorLoging(OracleUpdateStatement update) {
        if (identifierEquals("LOG")) {
            throw new ParserException("TODO");
        }
    }

    private void parseReturn(OracleUpdateStatement update) {
        if (identifierEquals("RETURN") || lexer.token() == Token.RETURNING) {
            lexer.nextToken();

            for (;;) {
                SQLExpr item = this.exprParser.expr();
                update.getReturning().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            accept(Token.INTO);

            for (;;) {
                SQLExpr item = this.exprParser.expr();
                update.getReturningInto().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }
    }

    private void parseHints(OracleUpdateStatement update) {
        this.exprParser.parseHints(update.getHints());
    }

    private void parseWhere(OracleUpdateStatement update) {
        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            update.setWhere(this.exprParser.expr());
        }
    }

}