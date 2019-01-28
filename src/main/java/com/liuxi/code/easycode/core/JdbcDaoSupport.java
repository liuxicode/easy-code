package com.liuxi.code.easycode.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * spring JdbcTemplate 封装
 *
 * @Auther: liuxi
 * @Date: 2019/1/18 17:53
 * @Description:
 */
@Repository("jdbcDaoSupport")
public class JdbcDaoSupport {

    private Logger logger = LoggerFactory.getLogger(JdbcDaoSupport.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * insert
     * @param table 表名
     * @param o Map<String,Object>对象或者Object对象
     *          （对象属性的遵循 下划线-驼峰原则,对象类型统一使用包装类,不使用基本数据类型,为了统一判空）
     * @return
     * @throws Exception
     */
    public int insertTable(String table, Object o) throws Exception {

        List<String> cloums = new ArrayList<String>();
        List<String> cloumValues = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();

        if(o instanceof Map){

            for (Object entry : ((Map<String,Object>)o).entrySet()) {

                Map.Entry entryMap = (Map.Entry) entry;

                String key = (String)entryMap.getKey();
                Object value = entryMap.getValue();

                if(key != null && value != null){
                    cloums.add(underscoreName(key));
                    cloumValues.add("?");
                    params.add(value);
                }
            }

        }else{
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field field : fields){

                String name = field.getName();

                name = name.substring(0,1).toUpperCase()+name.substring(1);
                try {
                    Method m = o.getClass().getMethod("get"+name);

                    Object value = m.invoke(o);

                    if(value != null){
                        cloums.add(underscoreName(name));
                        cloumValues.add("?");
                        params.add(value);
                    }

                } catch (NoSuchMethodException e) {
                    logger.error("DaoSupport insert fail:"+e.getMessage());
                    throw e;
                }
            }
        }

        String sql = "insert into "+table+"("+StringUtils.join(cloums,",") +") values("+StringUtils.join(cloumValues,",")+")";

        logger.info(sql);

        return jdbcTemplate.update(sql,params.toArray());
    }

    /**
     * update
     * @param table 表名
     * @param o  Map<String,Object>对象或者Object对象
     *          （对象属性的遵循 下划线-驼峰原则,对象类型统一使用包装类,不使用基本数据类型,为了统一判空）
     * @param whereSql
     * @param params
     * @return
     * @throws Exception
     */
    public int updateTable(String table, Object o, String whereSql, Object[] params) throws Exception{

        List<String> cloums = new ArrayList<String>();
        List<Object> datas = new ArrayList<Object>();

        if(o instanceof Map){

            for (Object entry : ((Map<String,Object>)o).entrySet()) {

                Map.Entry entryMap = (Map.Entry) entry;

                String key = (String)entryMap.getKey();
                Object value = entryMap.getValue();

                if(key != null && value != null){
                    cloums.add(underscoreName(key) + "= ?");
                    datas.add(value);
                }
            }

        }else{
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field field : fields){

                String name = field.getName();

                name = name.substring(0,1).toUpperCase()+name.substring(1);
                try {
                    Method m = o.getClass().getMethod("get"+name);

                    Object value = m.invoke(o);

                    if(value != null){
                        cloums.add(underscoreName(name)+"= ?");
                        datas.add(value);
                    }

                } catch (NoSuchMethodException e) {
                    logger.error("DaoSupport insert fail:"+e.getMessage());
                    throw e;
                }
            }
        }

        String sql = "update "+table+" set "+StringUtils.join(cloums,",")
                + " where 1=1 and "+whereSql;

        logger.info(sql);

        datas.addAll(Arrays.asList(params));

        return jdbcTemplate.update(sql,datas.toArray());

    }

    public <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {

        try {
            return jdbcTemplate.queryForObject(sql,args,rowMapper);
        }catch (Exception e){
            logger.error("queryForObject fail:"+e.getMessage());
        }

        return null;
    }

    public Map<String,Object> queryForMap(String sql, Object[] params){

        try {
            Map<String,Object> map = jdbcTemplate.queryForMap(sql,params);

            return map;

        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return null;

    }

    /**
     * 加时间格式化转换
     * @param sql
     * @param params
     * @param dateFormat
     * @return
     */
    public Map<String,Object> queryForMap(String sql, Object[] params, String dateFormat){

        SimpleDateFormat sdf = StringUtils.isNotBlank(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : null;


        try {
            Map<String,Object> map = jdbcTemplate.queryForMap(sql,params);

            if(map != null){
                for (Map.Entry<String,Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if(value instanceof Date && sdf != null){
                        entry.setValue(sdf.format(value));
                    }
                }
            }

            return map;
        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return null;

    }

    /**
     * 加时间格式化和驼峰转换
     * @param sql
     * @param params
     * @param dateFormat
     * @return
     */
    public Map<String,Object> queryForMapConverHump(String sql, Object[] params, String dateFormat){

        SimpleDateFormat sdf = StringUtils.isNotBlank(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : null;

        try {
            Map<String,Object> result = new HashMap<String, Object>();

            Map<String,Object> map = jdbcTemplate.queryForMap(sql,params);

            if(map != null){
                for (Map.Entry<String,Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if(value instanceof Date && sdf != null){
                        value = entry.setValue(sdf.format(value));
                    }

                    result.put(camelCaseName(key),value);
                }
            }

            return result;
        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return null;

    }

    public <T> List<T> query(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return jdbcTemplate.query(sql,args,rowMapper);
        }catch (Exception e){
            logger.error("queryForList fail:"+e.getMessage());
        }

        return new ArrayList<T>();
    }

    public List<Map<String,Object>> queryForList(String sql, Object[] params){

        try {
            return jdbcTemplate.queryForList(sql,params);
        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return new ArrayList<Map<String, Object>>();
    }

    /**
     * 加时间格式化转换
     * @param sql
     * @param params
     * @param dateFormat
     * @return
     */
    public List<Map<String,Object>> queryForList(String sql, Object[] params, String dateFormat){

        SimpleDateFormat sdf = StringUtils.isNotBlank(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : null;

        try {
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,params);

            if(list != null && list.size() > 0){
                for (Map<String,Object> map : list) {

                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if(value instanceof Date && sdf != null){
                            entry.setValue(sdf.format(value));
                        }
                    }

                }
            }

            return list;
        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return new ArrayList<Map<String, Object>>();
    }


    /**
     * 加时间格式化和驼峰转换
     * @param sql
     * @param params
     * @param dateFormat
     * @return
     */
    public List<Map<String,Object>> queryForListConverHump(String sql, Object[] params, String dateFormat){

        SimpleDateFormat sdf = StringUtils.isNotBlank(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : null;

        try {
            List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();

            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,params);

            if(list != null && list.size() > 0){

                for (Map<String,Object> map : list) {

                    Map<String,Object> data = new HashMap<String, Object>();

                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if(value instanceof Date && sdf != null){
                            value = sdf.format(value);
                        }

                        data.put(camelCaseName(key),value);
                    }

                    datas.add(data);

                }
            }

            return datas;
        }catch (Exception e){
            logger.error("queryForMap fail:"+e.getMessage());
        }

        return new ArrayList<Map<String, Object>>();
    }

    /**
     * 转换为下划线
     * @param camelCaseName
     * @return
     */
    public static String underscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 转换为驼峰
     *
     * @param underscoreName
     * @return
     */
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

}
