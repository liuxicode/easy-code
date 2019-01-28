package com.liuxi.code.easycode.service;

import com.liuxi.code.easycode.core.JdbcDaoSupport;
import com.liuxi.code.easycode.dao.User;
import com.liuxi.code.easycode.dao.UserModle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liuxi
 * @Date: 2019/1/18 16:13
 * @Description:
 */
@Service
public class TestService {

    private static Logger logger = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcDaoSupport daoSupport;

    public User test(){

        User user = jdbcTemplate.queryForObject("select id,user_name,password,add_time from user where id = ?",new Object[]{1},new User());


        List<UserModle> modles = jdbcTemplate.query("select id,user_name,password,add_time from user where id = 1", new Object[]{}, new BeanPropertyRowMapper(UserModle.class));

        UserModle modle = jdbcTemplate.queryForObject("select id,user_name,password,add_time from user where id = 1", new Object[]{}, (RowMapper<UserModle>) new BeanPropertyRowMapper(UserModle.class));


        return user;
    }

    @Transactional
    public int add(User user){

        String sql = "insert into user(user_name,password,add_time) values(?,?,?)";

        Object[] param = new Object[]{user.getUserName(),user.getPassword(),user.getAddTime()};

        int result = jdbcTemplate.update(sql,param);



        return result;
    }

    @Transactional
    public int testDaoSupport() throws Exception {

        Map<String,Object> map = new HashMap<String, Object>();

        map.put("user_name","test1");
        map.put("password","test1");
        map.put("age",1);
        map.put("add_time","2018-12-12 00:00:00");

        try{
            daoSupport.insertTable("user",map);
        }catch (Exception e){
            logger.info("inset data fail:"+e.getMessage());
        }


        UserModle userModle = new UserModle();
        userModle.setUserName("test2");
        userModle.setPassword("test2");
        userModle.setAge(1);
        userModle.setAddTime("2018-10-10 00:00:00");

        try{
            daoSupport.insertTable("user",userModle);
        }catch (Exception e){
            logger.info("inset data fail:"+e.getMessage());
        }


        User user = new User();
        user.setUserName("test3");
        user.setPassword("test3");
        user.setAge(1);
        user.setAddTime("2018-12-12 00:00:00");

        try{
            daoSupport.insertTable("user",user);
        }catch (Exception e){
            logger.info("inset data fail:"+e.getMessage());
        }

        return 1;
    }

    @Transactional
    public int updateSupport() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        UserModle userModle = new UserModle();
        userModle.setUserName("liuxi1");
        userModle.setPassword("1232321321321");
        userModle.setAddTime("2017-10-10 00:00:00");

        int result = daoSupport.updateTable("user" ,userModle,"id = ?",new Object[]{1});

        User user = new User();
        user.setUserName("liuxi2");
        user.setPassword("liuxi2");
        user.setAge(1);
        user.setAddTime("2018-12-12 00:00:00");


        daoSupport.insertTable("user",user);


        return 1;
    }

    public Object testDaoSupportQuery(){
        List<Map<String,Object>> list = daoSupport.queryForList("select * from user",null,"yyyy-MM-dd HH:mm:ss");

        List<User> list1 = daoSupport.query("select * from user",null,new User());

        List<UserModle> list2 = daoSupport.query("select * from user",null,(RowMapper<UserModle>) new BeanPropertyRowMapper(UserModle.class));

        Map<String,Object> map = daoSupport.queryForMap("select * from user where id = 1",null,"yyyy-MM-dd HH:mm:ss");

        Map<String,Object> map2 = daoSupport.queryForMapConverHump("select * from user where id = 1",null,"yyyy-MM-dd HH:mm:ss");

        User user = daoSupport.queryForObject("select * from user where id = ?",new Object[]{1},new User());
        UserModle userModle = daoSupport.queryForObject("select * from user where id = ?",new Object[]{1}, (RowMapper<UserModle>) new BeanPropertyRowMapper(UserModle.class));



        return daoSupport.queryForListConverHump("select * from user",null,"yyyy-MM-dd HH:mm:ss");
    }
}
