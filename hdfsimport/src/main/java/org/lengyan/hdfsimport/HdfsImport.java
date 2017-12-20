package org.lengyan.hdfsimport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.lengyan.bean.SinaWeibo;

import java.io.IOException;
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
        System.setProperty("hadoop.home.dir", "E:\\ideaspace\\hadoop-2.7.4");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "HdfsImport");
        job.setJarByClass(HdfsImport.class);
        job.setMapperClass(HdfsImportMapper.class);        job.setReducerClass(HdfsImportReducer.class);


        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver", "jdbc:mysql://119.23.10.175:3306/ly_smart?characterEncoding=UTF-8","root","hongfeng123456");
        String [] fields = {"user_name", "user_url", "address", "abstractContent"};
        DBInputFormat.setInput(job, SinaWeibo.class,"T_SINA_WEIBO", null, null, fields);
        FileOutputFormat.setOutputPath(job, new Path("hdfs://127.0.0.1:9000/hdfsFile"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }


}
