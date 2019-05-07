/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ${project_package};

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT_DISTINCT;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Date;
import ${project_package}.${entity_class};
import ${project_package}.${entity_class}Example.Criteria;
import ${project_package}.${entity_class}Example.Criterion;
import ${project_package}.${entity_class}Example;

/**
* Created by mydel2mybatis tool
* @description ${entity_comment}
*/
public class ${entity_class}SqlProvider {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String countByExample(${entity_class}Example example) {
        BEGIN();
        SELECT("count(*)");
        FROM("${entity_table_name}");
        applyWhere(example, false);
        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String deleteByExample(${entity_class}Example example) {
        BEGIN();
        DELETE_FROM("${entity_table_name}");
        applyWhere(example, false);
        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String insertSelective(${entity_class} record) {
        BEGIN();
        INSERT_INTO("${entity_table_name}");

        <#list class_columns as col>
        if (record.get${col.upcaseCamelName}() != null) {
            VALUES("${col.jdbcName}", "<#noparse>#{</#noparse>${col.name},jdbcType=${col.jdbcClazz}<#noparse>}</#noparse>");
        }
        </#list>

        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String selectByExample(${entity_class}Example example) {
        BEGIN();
        if (example != null && example.isDistinct()) {
            SELECT_DISTINCT("<#list class_primarkKeys as key><#if key_index != 0>,</#if>${key.jdbcName}</#list>");
        } else {
            SELECT("<#list class_primarkKeys as key><#if key_index != 0>,</#if>${key.jdbcName}</#list>");
        }
        <#list class_notKeys as item>
        SELECT("${item.jdbcName}");
        </#list>

        FROM("${entity_table_name}");
        applyWhere(example, false);

        if (example != null && example.getOrderByClause() != null) {
            ORDER_BY(example.getOrderByClause());
        }

        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String updateByExampleSelective(Map<String, Object> parameter) {
        ${entity_class} record = (${entity_class}) parameter.get("record");
        ${entity_class}Example example = (${entity_class}Example) parameter.get("example");

        BEGIN();
        UPDATE("${entity_table_name}");

        <#list class_columns as col>
        if (record.get${col.upcaseCamelName}() != null) {
            SET("${col.jdbcName} = <#noparse>#{</#noparse>record.${col.name},jdbcType=${col.jdbcClazz}<#noparse>}</#noparse>");
        }
        </#list>

        applyWhere(example, true);
        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String updateByExample(Map<String, Object> parameter) {
        BEGIN();
        UPDATE("${entity_table_name}");

        <#list class_columns as col>
        SET("${col.jdbcName} = <#noparse>#{</#noparse>record.${col.name},jdbcType=${col.jdbcClazz}<#noparse>}</#noparse>");
        </#list>

        ${entity_class}Example example = (${entity_class}Example) parameter.get("example");
        applyWhere(example, true);
        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String updateByPrimaryKeySelective(${entity_class} record) {
        BEGIN();
        UPDATE("${entity_table_name}");

        <#list class_notKeys as item>
        if (record.get${item.upcaseCamelName}() != null) {
            SET("${item.jdbcName} = <#noparse>#{</#noparse>${item.name},jdbcType=${item.jdbcClazz}<#noparse>}</#noparse>");
        }
        </#list>

        WHERE("1=1"<#list class_primarkKeys as key>
            + " and ${key.jdbcName} = <#noparse>#{</#noparse>${key.name},jdbcType=${key.jdbcClazz}<#noparse>}</#noparse>"</#list>
            );

        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected void applyWhere(${entity_class}Example example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }

        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s <#noparse>#{</#noparse>example.oredCriteria[%d].allCriteria[%d].value<#noparse>}</#noparse>";
            parmPhrase1_th = "%s <#noparse>#{</#noparse>example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s<#noparse>}</#noparse>";
            parmPhrase2 = "%s <#noparse>#{example.oredCriteria[%d].allCriteria[%d].value<#noparse>}</#noparse> and <#noparse>#{</#noparse>example.oredCriteria[%d].criteria[%d].secondValue<#noparse>}</#noparse>";
            parmPhrase2_th = "%s <#noparse>#{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s<#noparse>}</#noparse> and <#noparse>#{</#noparse>example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s<#noparse>}</#noparse>";
            parmPhrase3 = "<#noparse>#{</#noparse>example.oredCriteria[%d].allCriteria[%d].value[%d]<#noparse>}</#noparse>";
            parmPhrase3_th = "<#noparse>#{</#noparse>example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s<#noparse>}</#noparse>";
        } else {
            parmPhrase1 = "%s <#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value<#noparse>}</#noparse>";
            parmPhrase1_th = "%s <#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value,typeHandler=%s<#noparse>}</#noparse>";
            parmPhrase2 = "%s <#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value<#noparse>}</#noparse> and <#noparse>#{</#noparse>oredCriteria[%d].criteria[%d].secondValue<#noparse>}</#noparse>";
            parmPhrase2_th = "%s <#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value,typeHandler=%s<#noparse>}</#noparse> and <#noparse>#{</#noparse>oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s<#noparse>}</#noparse>";
            parmPhrase3 = "<#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value[%d]<#noparse>}</#noparse>";
            parmPhrase3_th = "<#noparse>#{</#noparse>oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s<#noparse>}</#noparse>";
        }

        StringBuilder sb = new StringBuilder();
        List<Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }

                sb.append('(');
                List<Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }

                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }

        if (sb.length() > 0) {
            WHERE(sb.toString());
        }
    }
}