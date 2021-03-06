package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcSupport;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by xuji on 2017/5/31.
 */
@Test(priority = 20)
public class TestGeneralKeys  {
    private String testSql = "INSERT INTO  table_inc_id(status) VALUES(?)";

    public void testGeneratedKeys() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(testSql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, "1");
        int result = preparedStatement.executeUpdate();
        TestSupport.logger.info("result=" + result);
        ResultSet rs = preparedStatement.getGeneratedKeys();
        TestSupport.printResultSet(rs);
        connection.commit();
        connection.close();
    }

    public void testGeneratedKeysBatch() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(testSql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, "1");
        preparedStatement.addBatch();
        preparedStatement.setString(1, "1");
        preparedStatement.addBatch();

        int[] result = preparedStatement.executeBatch();
        TestSupport.logger.info("result=" + Arrays.toString(result));
        ResultSet rs = preparedStatement.getGeneratedKeys();
        TestSupport.printResultSet(rs);
        connection.commit();
        connection.close();
    }


}
