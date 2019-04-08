/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mpango.core;

import com.mpango.core.db.MysqlConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jamulutu
 */
public class Task {

    private static MysqlConnect db;
    private static Connection con;

    public Task() {
    }

    public void getActiveTasks() {
        db = MysqlConnect.getDbCon();
        con = db.conn;

        String SQL = "SELECT * FROM tasks WHERE active = 1 AND task_date = '" + getDateString(1) + "'";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.prepareStatement(SQL);
            rs = st.executeQuery(SQL);
            while (rs.next()) {
                int taskId = rs.getInt("id");
                String taskName = rs.getString("task_name");
                String taskDesc = rs.getString("description");
                String message = "TASK FOR " + getDateString(1) + ": " + taskDesc;

                String SQL2 = "INSERT INTO `sms_alerts` (`id`, `source_id`, `source_msg_id`, `date_received`, `destination_msisdn`, `message`, `date_sent`, `delivery_response`) VALUES (NULL, '1', ?, NOW(), ?, ?, NULL, '')";
                PreparedStatement stmt = con.prepareStatement(SQL2);
                stmt.setInt(1, taskId);
                stmt.setString(2, "254720844418");
                stmt.setString(3, message);
                
                int resultInsert = stmt.executeUpdate();
                System.out.println("SQL SELECT: " + SQL );
                System.out.println("SQL insert: " + SQL2 );
                System.out.println("RESULT INT: " + resultInsert );

                if (resultInsert == 1) {
                    updateTask(taskId);
                }
            }
            rs.close();
            //con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTask(int taskId) {
        String SQL = "UPDATE tasks SET active=0 WHERE id = " + taskId;
        SQLUpdate(SQL, con);
    }

    public int SQLUpdate(String SQL, Connection con) {
        int result = 0;
        Statement sttm = null;
        try {
            sttm = con.prepareStatement(SQL);
            int rowsUpdated = sttm.executeUpdate(SQL);
            if (rowsUpdated > 0) {
                result = 1;
            }
            sttm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //DbUtils.closeQuietly(rs);
            //DbUtils.closeQuietly(sttm);
            //DbUtils.closeQuietly(con);
        }
        return result;
    }

    public int SQLInsert(String SQL, Connection con) {
        int result = 0;
        Statement st = null;

        try {
            st = con.prepareCall(SQL);
            int rowsUpdated = st.executeUpdate(SQL);
            if (rowsUpdated > 0) {
                result = 1;
            }
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //DbUtils.closeQuietly(rs);
            //DbUtils.closeQuietly(st);
            //DbUtils.closeQuietly(con);
        }
        return result;
    }

    private String getDateString(int daysOffset) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, daysOffset);
        dt = c.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(dt);

        return dateString;
    }

}
