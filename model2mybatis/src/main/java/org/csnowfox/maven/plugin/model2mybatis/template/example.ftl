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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
* Created by mydel2mybatis tool
* @description ${entity_comment}
*/
public class ${entity_class}Example {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public ${entity_class}Example() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table ${entity_table_name}
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        <#list class_columns as col>
        public Criteria and${col.upcaseCamelName}IsNull() {
            addCriterion("${col.name} is null");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}IsNotNull() {
            addCriterion("${col.name} is not null");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}EqualTo(${col.clazz} value) {
            addCriterion("${col.name} =", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}NotEqualTo(${col.clazz} value) {
            addCriterion("${col.name} <>", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}GreaterThan(${col.clazz} value) {
            addCriterion("${col.name} >", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}GreaterThanOrEqualTo(${col.clazz} value) {
            addCriterion("${col.name} >=", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}LessThan(${col.clazz} value) {
            addCriterion("${col.name} <", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}LessThanOrEqualTo(${col.clazz} value) {
            addCriterion("${col.name} <=", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}Like(${col.clazz} value) {
            addCriterion("${col.name} like", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}NotLike(${col.clazz} value) {
            addCriterion("${col.name} not like", value, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}In(List<${col.clazz}> values) {
            addCriterion("${col.name} in", values, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}NotIn(List<${col.clazz}> values) {
            addCriterion("${col.name} not in", values, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}Between(${col.clazz} value1, ${col.clazz} value2) {
            addCriterion("${col.name} between", value1, value2, "${col.name}");
            return (Criteria) this;
        }

        public Criteria and${col.upcaseCamelName}NotBetween(${col.clazz} value1, ${col.clazz} value2) {
            addCriterion("${col.name} not between", value1, value2, "${col.name}");
            return (Criteria) this;
        }

        </#list>

    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table xx
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table xx
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}