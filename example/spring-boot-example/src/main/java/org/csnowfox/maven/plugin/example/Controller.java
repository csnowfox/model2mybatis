package org.csnowfox.maven.plugin.example;

import org.csnowfox.maven.plugin.example.dao.fund.FundCalendar;
import org.csnowfox.maven.plugin.example.dao.fund.FundCalendarExample;
import org.csnowfox.maven.plugin.example.dao.fund.FundCalendarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName: Controller
 * @Description TODO
 * @Author Csnowfox
 * @Date 2019/4/27 19:18
 **/

@org.springframework.stereotype.Controller
@RequestMapping("/fund")
public class Controller {

    @Autowired
    private FundCalendarMapper fundCalendarMapper;

    @RequestMapping("/showFund")
    @ResponseBody
    public List<FundCalendar> toIndex(){
        FundCalendar fundCalendar = new FundCalendar();
        fundCalendar.setFundcode(String.valueOf(((int)(Math.random() * 1000))));
        fundCalendar.setIdate("20190101");
        fundCalendar.setTaid("1");
        fundCalendarMapper.insert(fundCalendar);
        List<FundCalendar> fundCalendars = fundCalendarMapper.selectByExample(new FundCalendarExample());
        return fundCalendars;
    }

}
