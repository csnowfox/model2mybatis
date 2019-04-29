package org.csnowfox.maven.plugin.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.csnowfox.maven.plugin.example.dao.fund.FundCalendar;
import org.csnowfox.maven.plugin.example.dao.fund.FundCalendarExample;
import org.csnowfox.maven.plugin.example.dao.fund.FundCalendarMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: Application
 * @Description TODO
 * @Author Csnowfox
 * @Date 2019/4/27 23:10
 **/
public class Application {

    public static void main(String[] args) throws IOException {

        String resource = "mybatis-config.xml";
        InputStream ins = Resources.getResourceAsStream(resource);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(ins);
        sessionFactory.getConfiguration().addMapper(FundCalendarMapper.class);
        SqlSession session = sessionFactory.openSession();

        FundCalendarExample example = new FundCalendarExample();
        List<FundCalendar> result = session.getMapper(FundCalendarMapper.class).selectByExample(example);
        for (int i =0; i < result.size(); i++) {
            System.out.println("#1:" + result.get(i).getFundcode());
        }

    }
}
