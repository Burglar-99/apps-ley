package mx.com.azteca.home.entity.generator;


import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    private List<QueryField> queryConditions;

    public QueryBuilder() {
        this.queryConditions = new ArrayList<>();
    }

    public QueryBuilder and(String key, Object value) {
        QueryField queryField = new QueryField();
        queryField.key = key;
        queryField.value = value;
        queryField.condition = Conditions.AND;
        queryConditions.add(queryField);
        return this;
    }

    public QueryBuilder or(String key, Object value) {
        QueryField queryField = new QueryField();
        queryField.key = key;
        queryField.value = value;
        queryField.condition = Conditions.OR;
        queryConditions.add(queryField);
        return this;
    }

    public List<QueryField> getQueryConditions() {
        return queryConditions;
    }

    public enum Conditions {

        AND("AND"),
        OR("OR"),
        IN("IN");

        public String CONDITION;

        Conditions(String condition) {
            this.CONDITION = condition;
        }

    }

    public class QueryField {
        public String key;
        public Object value;
        public Conditions condition;
    }
}
