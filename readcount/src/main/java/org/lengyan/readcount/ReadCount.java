package org.lengyan.readcount;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.lengyan.readcount.bean.UserInfoRecord;
import org.lengyan.readcount.output.CityStat;

import java.io.IOException;

/**
 * mapreduce读取mysql数据库中的记录
 * Created by kangtiancheng on 2017/12/14.
 */
public class ReadCount {

    public static class ReadCountMapper extends Mapper<LongWritable, UserInfoRecord, Text, CityStat> {
        @Override
        protected void map(LongWritable key, UserInfoRecord value, Context context) throws IOException, InterruptedException {
            String address = value.getAddress();
            String cityName = null;
            if (StringUtils.isNotBlank(address)) {
                cityName = address.substring(0,2);
                CityStat cityStat = new CityStat(cityName, 1);
                context.write(new Text(cityName), cityStat);
                System.out.println(cityStat.toString());
            }
        }
    }

    public static class ReadCountReduce extends Reducer<Text, CityStat, CityStat, NullWritable> {
        NullWritable n = NullWritable.get();
        @Override
        protected void reduce(Text key, Iterable<CityStat> values, Context context) throws IOException, InterruptedException {
            Integer cityCount = 0;
            for(CityStat bean : values) {
                cityCount +=bean.getCityCount();
            }
            CityStat bean = new CityStat(key.toString(), cityCount);
            context.write(bean, n);
            System.out.println(bean);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir", "E:\\ideaspace\\hadoop-2.7.4");
        Configuration conf = new Configuration();
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver", "jdbc:mysql://119.23.10.175:3306/ly_smart??characterEncoding=utf8&useSSL=false","root","hongfeng123456");
        String [] fields = {"user_name", "user_url", "address", "abstractContent"};

        Job job = Job.getInstance(conf, "readcount");
        job.setJarByClass(ReadCount.class);
        job.setInputFormatClass(DBInputFormat.class);
        DBInputFormat.setInput(job, UserInfoRecord.class, "T_SINA_USER", null, null, fields);
        job.setMapperClass(ReadCountMapper.class);
        job.setReducerClass(ReadCountReduce.class);
        job.setCombinerClass(ReadCountReduce.class);
        job.setOutputKeyClass(CityStat.class);
        job.setOutputValueClass(NullWritable.class);
        DBOutputFormat.setOutput(job, "cityStat", "cityName","cityCount");
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}