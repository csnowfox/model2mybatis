package org.csnowfox.maven.plugin.pdm2mybatis.entity;

/**
 * @ClassName: ClassColumns
 * @Description 栏位
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class ClassColumns {

    // pojo 驼峰名称
    private String name;
    // pojo 类型
    private String clazz;
    // jdbc 类型
    private String jdbcClazz;
    // pojo 大写驼峰
    private String upcaseCamelName;
    // 注释
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getUpcaseCamelName() {
        return upcaseCamelName;
    }

    public void setUpcaseCamelName(String upcaseCamelName) {
        this.upcaseCamelName = upcaseCamelName;
    }

    public String getJdbcClazz() {
        return jdbcClazz;
    }

    public void setJdbcClazz(String jdbcClazz) {
        this.jdbcClazz = jdbcClazz;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
