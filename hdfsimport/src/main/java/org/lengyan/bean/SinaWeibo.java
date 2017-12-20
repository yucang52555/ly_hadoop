package org.lengyan.bean;

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
 * 新浪微博信息
 * Created by kangtiancheng on 2017/12/20.
 */
public class SinaWeibo implements Writable, DBWritable{

    private String weiboUrl;
    private String pubTime;
    private String pubContent;

    public SinaWeibo() {
    }

    public SinaWeibo(String weiboUrl, String pubTime, String pubContent) {
        this.weiboUrl = weiboUrl;
        this.pubTime = pubTime;
        this.pubContent = pubContent;
    }

    public String getWeiboUrl() {
        return weiboUrl;
    }

    public void setWeiboUrl(String weiboUrl) {
        this.weiboUrl = weiboUrl;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getPubContent() {
        return pubContent;
    }

    public void setPubContent(String pubContent) {
        this.pubContent = pubContent;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, this.weiboUrl);
        Text.writeString(dataOutput, this.pubTime);
        Text.writeString(dataOutput, this.pubContent);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.weiboUrl = Text.readString(dataInput);
        this.pubTime = Text.readString(dataInput);
        this.pubContent = Text.readString(dataInput);
    }

    @Override
    public void write(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, weiboUrl);
        preparedStatement.setString(2, pubTime);
        preparedStatement.setString(3, pubContent);
    }

    @Override
    public void readFields(ResultSet resultSet) throws SQLException {
        this.weiboUrl = resultSet.getString("weibo_url");
        this.pubTime = resultSet.getString("pub_time");
        this.pubContent = resultSet.getString("pub_content");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
