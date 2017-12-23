package org.lengyan.hdfsimport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.lengyan.bean.SinaWeibo;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

/**
 * 读取mysql数据写入hadoop的hdfs
 * Created by kangtiancheng on 2017/12/19.
 */
public class HdfsImport {

    public static  class HdfsImportMapper extends Mapper<LongWritable, SinaWeibo, Text, Text>{
        @Override
        protected void map(LongWritable key, SinaWeibo value, Context context) throws IOException, InterruptedException {
            System.out.println(value.toString());
            context.write(new Text(), new Text(value.toString()));
        }
    }

    public static  class HdfsImportReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Iterator<Text> itr = values.iterator(); itr.hasNext();) {
                context.write(key, itr.next());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir", "F:\\kingkang\\ly_hadoop\\hadoop-2.7.4");
        Configuration conf = new Configuration();
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver", "jdbc:mysql://119.23.10.175:3306/ly_smart?characterEncoding=UTF-8","root","hongfeng123456");
        Job job = Job.getInstance(conf, "HdfsImport");
        job.setJarByClass(HdfsImport.class);
        job.setMapperClass(HdfsImportMapper.class);
        job.setReducerClass(HdfsImportReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        String [] fields = {"weibo_url", "pub_time", "pub_content"};
        DBInputFormat.setInput(job, SinaWeibo.class,"T_SINA_WEIBO", null, null, fields);
        FileOutputFormat.setOutputPath(job, new Path("F:\\kingkang\\workspace\\ly_hadoop\\hdfsimport\\output"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}
