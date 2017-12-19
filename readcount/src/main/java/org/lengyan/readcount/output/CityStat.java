package org.lengyan.readcount.output;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 城市统计结果
 * Created by kangtiancheng on 2017/12/14.
 */
public class CityStat implements WritableComparable<CityStat>, Writable, DBWritable {

    private String cityName;
    private Long cityCount;

    public CityStat(){}

    public CityStat(String cityName, Long cityCount) {
        this.cityName = cityName;
        this.cityCount = cityCount;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getCityCount() {
        return cityCount;
    }

    public void setCityCount(Long cityCount) {
        this.cityCount = cityCount;
    }

    @Override
    public int compareTo(CityStat o) {
        return o.cityCount > 0 ? 0 : 1;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(cityName);
        dataOutput.writeLong(cityCount);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        cityName = dataInput.readUTF();
        cityCount = dataInput.readLong();
    }

    @Override
    public void write(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.cityName);
        preparedStatement.setLong(2, this.cityCount);
    }

    @Override
    public void readFields(ResultSet resultSet) throws SQLException {
        this.cityName = resultSet.getString(1);
        this.cityCount = resultSet.getLong(2);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
