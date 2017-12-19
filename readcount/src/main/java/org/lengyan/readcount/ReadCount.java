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
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.lengyan.readcount.bean.UserInfoRecord;
import org.lengyan.readcount.output.CityStat;

import java.io.IOException;

/**
 * mapreduce读取mysql数据库中的记录
 * Created by kangtiancheng on 2017/12/14.
 */
public class ReadCount {

    public static class ReadCountMapper extends Mapper<LongWritable, UserInfoRecord, Text, LongWritable> {
        LongWritable ONE = new LongWritable(1L);
        @Override
        protected void map(LongWritable key, UserInfoRecord value, Context context) throws IOException, InterruptedException {
            String address = value.getAddress();
            if (StringUtils.isNotBlank(address)) {
                String cityName = address.substring(0,2);
                context.write(new Text(cityName), ONE);
                System.out.println(cityName.toString());
            }
        }
    }

    public static class ReadCountReduce extends Reducer<Text, LongWritable, CityStat, NullWritable> {
        NullWritable n = NullWritable.get();
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            Long sum = 0L;
            for(LongWritable count : values) {
                sum +=count.get();
            }
            CityStat bean = new CityStat(key.toString(), sum);
            context.write(bean, n);
            System.out.println(bean);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir", "E:\\ideaspace\\hadoop-2.7.4");
        Configuration conf = new Configuration();
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver", "jdbc:mysql://119.23.10.175:3306/ly_smart?characterEncoding=UTF-8","root","hongfeng123456");
        String [] fields = {"user_name", "user_url", "address", "abstractContent"};
        Job job = Job.getInstance(conf, "readcount");
        job.setJarByClass(ReadCount.class);

        job.setMapperClass(ReadCountMapper.class);
        job.setCombinerClass(LongSumReducer.class);
        job.setReducerClass(ReadCountReduce.class);

        DBInputFormat.setInput(job, UserInfoRecord.class, "T_SINA_USER", null, null, fields);
        DBOutputFormat.setOutput(job, "T_SINA_CITY_STAT", "city_name","city_count");

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(CityStat.class);
        job.setOutputValueClass(NullWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}