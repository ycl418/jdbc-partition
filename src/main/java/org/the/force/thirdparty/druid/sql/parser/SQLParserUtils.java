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
package org.the.force.thirdparty.druid.sql.parser;

import org.the.force.thirdparty.druid.sql.dialect.db2.parser.DB2Lexer;
import org.the.force.thirdparty.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.odps.parser.OdpsExprParser;
import org.the.force.thirdparty.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.parser.PGExprParser;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.parser.PGLexer;
import org.the.force.thirdparty.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import org.the.force.thirdparty.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.db2.parser.DB2ExprParser;
import org.the.force.thirdparty.druid.sql.dialect.db2.parser.DB2StatementParser;
import org.the.force.thirdparty.druid.sql.dialect.mysql.parser.MySqlExprParser;
import org.the.force.thirdparty.druid.sql.dialect.mysql.parser.MySqlLexer;
import org.the.force.thirdparty.druid.sql.dialect.odps.parser.OdpsLexer;
import org.the.force.thirdparty.druid.sql.dialect.odps.parser.OdpsStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.the.force.thirdparty.druid.sql.dialect.oracle.parser.OracleLexer;
import org.the.force.thirdparty.druid.sql.dialect.phoenix.parser.PhoenixExprParser;
import org.the.force.thirdparty.druid.sql.dialect.phoenix.parser.PhoenixLexer;
import org.the.force.thirdparty.druid.sql.dialect.phoenix.parser.PhoenixStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import org.the.force.thirdparty.druid.sql.dialect.sqlserver.parser.SQLServerLexer;
import org.the.force.thirdparty.druid.util.JdbcConstants;
import org.the.force.thirdparty.druid.util.JdbcUtils;

public class SQLParserUtils {

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType) {
        boolean keepComments;
        if (JdbcConstants.ODPS.equals(dbType) || JdbcConstants.MYSQL.equals(dbType)) {
            keepComments = true;
        } else {
            keepComments = false;
        }
        return createSQLStatementParser(sql, dbType, keepComments);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, boolean keepComments) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleStatementParser(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlStatementParser(sql, keepComments);
        }

        if (JdbcUtils.MARIADB.equals(dbType)) {
            return new MySqlStatementParser(sql, keepComments);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGSQLStatementParser(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerStatementParser(sql);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlStatementParser(sql);
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2StatementParser(sql);
        }
        
        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsStatementParser(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixStatementParser(sql);
        }

        return new SQLStatementParser(sql, dbType);
    }

    public static SQLExprParser createExprParser(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleExprParser(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
            return new MySqlExprParser(sql);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGExprParser(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerExprParser(sql);
        }

        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2ExprParser(sql);
        }

        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsExprParser(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixExprParser(sql);
        }

        return new SQLExprParser(sql);
    }

    public static Lexer createLexer(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleLexer(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
                JdbcUtils.MARIADB.equals(dbType) || //
                JdbcUtils.H2.equals(dbType)) {
            return new MySqlLexer(sql);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGLexer(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerLexer(sql);
        }

        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2Lexer(sql);
        }

        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsLexer(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixLexer(sql);
        }

        return new Lexer(sql);
    }
}
