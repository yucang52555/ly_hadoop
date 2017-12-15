package org.lengyan.readcount.bean;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户信息记录
 */
public class UserInfoRecord implements Writable, DBWritable {

        private String userName;

        private String userUrl;

        private String address;

        private String abstractContent;

        public UserInfoRecord(){}

        public UserInfoRecord(String userName, String userUrl, String address, String abstractContent) {
                this.userName = userName;
                this.userUrl = userUrl;
                this.address = address;
                this.abstractContent = abstractContent;
        }

        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public String getUserUrl() {
                return userUrl;
        }

        public void setUserUrl(String userUrl) {
                this.userUrl = userUrl;
        }

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public String getAbstractContent() {
                return abstractContent;
        }

        public void setAbstractContent(String abstractContent) {
                this.abstractContent = abstractContent;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
                Text.writeString(dataOutput, this.userName);
                Text.writeString(dataOutput, this.userUrl);
                Text.writeString(dataOutput, this.address);
                Text.writeString(dataOutput, this.abstractContent);
        }


        @Override
        public void readFields(DataInput dataInput) throws IOException {
                this.userName = Text.readString(dataInput);
                this.userUrl = Text.readString(dataInput);
                this.address = Text.readString(dataInput);
                this.abstractContent = Text.readString(dataInput);
        }

        @Override
        public void write(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, userUrl);
                preparedStatement.setString(3, address);
                preparedStatement.setString(4, abstractContent);
        }

        @Override
        public void readFields(ResultSet resultSet) throws SQLException {
                this.userName = resultSet.getString("user_name");
                this.userUrl = resultSet.getString("user_url");
                this.address = resultSet.getString("address");
                this.abstractContent = resultSet.getString("abstractContent");
        }

        @Override
        public String toString() {
                return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
}