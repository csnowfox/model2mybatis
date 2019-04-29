package org.csnowfox.maven.plugin.model2mybatis.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Ddl
 * @Description DDL 生成
 * @Author Csnowfox
 * @Date 2019/4/27 16:58
 **/
public class Ddl {

    public static List<String> generateSQL(List<Table> tabs) {
        List<String> lismsg = new ArrayList<String>();

        lismsg.add("spool execute.log\n");
        lismsg.add("\n");

        for (int i = 0; i < tabs.size(); i++) {
            Table tab = tabs.get(i);
            Column[] cols = tab.getCols();
            String tabName = tab.getUser().getName() + "." + tab.getTableCode();
            Key pk = tab.getPrimaryKey();

            List<String> createTabNsg = generateTableSQL(tabName, cols, pk);
            lismsg.addAll(createTabNsg);
            lismsg.add("\n");

            if (pk != null && pk.getColumnId() != null) {
                lismsg.add(generatePKSQL(tab, tabName, cols, pk));
            }
            lismsg.add("\n");

            lismsg.add(generateTabCommentSQL(tab, tabName));
            lismsg.add("\n");

            List<String> commentMsg = generateColCommentSQL(tabName, cols);
            lismsg.addAll(commentMsg);
            lismsg.add("\n");
        }

        lismsg.add("spool off\n");

        return lismsg;
    }

    private static Column find(Column [] cols, String code) {
        if (cols == null)
            return null;

        for (int i = 0 ; i < cols.length ; ++i) {
            if (code.equals(cols[i].getCode())) {
                return cols[i];
            }
        }

        return null;
    }

    private static List<String> generateTableSQL(String tabName, Column[] cols, Key pk) {
        List<String> lismsg = new ArrayList<String>();

        Column [] keyCols = null;
        if (pk != null) {
            keyCols = pk.qryColumn(cols);
        }

        lismsg.add("\n");
        lismsg.add("prompt\n");
        lismsg.add("prompt Model2MybatisJavaCode table " + tabName + "\n");
        lismsg.add("prompt =================================\n");
        lismsg.add("prompt\n");
        lismsg.add("prompt\n");
        lismsg.add("create table " + tabName + "\n");
        lismsg.add("(\n");
        for (int j = 0 ; j < cols.length ; ++j) {
            StringBuffer columnBuf = new StringBuffer();

            columnBuf.append("  ")
                    .append(String.format("%-20s", cols[j].getCode()))
                    .append(" ")
                    .append(String.format("%-15s", cols[j].getTypefull()));

            Column keyCol = find(keyCols, cols[j].getCode());
            if (keyCol != null) {
                columnBuf.append(" not null");
            }
            if (j < cols.length - 1) {
                columnBuf.append(",");
            }
            columnBuf.append("\n");

            lismsg.add(columnBuf.toString());
        }
        lismsg.add(");\n");

        return lismsg;
    }

    private static String generatePKSQL(Table tab, String tabName, Column[] cols, Key pk) {
        StringBuffer keyBuf = new StringBuffer();
        Column [] keyCols = pk.qryColumn(cols);

        StringBuffer key = new StringBuffer();
        for (int j = 0 ; j < keyCols.length ; ++j) {
            key.append(keyCols[j].getCode());
            if (j < keyCols.length - 1) {
                key.append(",");
            }
        }

        keyBuf.append("alter table ")
                .append(tabName)
                .append(" add constraint PK_")
                .append(tab.getTableCode())
                .append(" primary key")
                .append("(" + key + ");\n");

        return keyBuf.toString();
    }

    private static String generateTabCommentSQL(Table tab, String tabName) {
        StringBuffer commentBuf = new StringBuffer();

        String comment = "";
        if (tab.getComment() != null && !tab.getComment().isEmpty()) {
            comment = tab.getComment();
        }
        else {
            comment = tab.getTableName();
        }

        commentBuf.append("comment on table ")
                .append(tabName)
                .append(" is '")
                .append(comment)
                .append("';\n");

        return commentBuf.toString();
    }

    private static List<String> generateColCommentSQL(String tabName, Column[] cols) {
        List<String> lismsg = new ArrayList<String>();

        for (int j = 0 ; j < cols.length ; ++j) {
            StringBuffer colCommentBuf = new StringBuffer();

            String colComment = "";
            if (cols[j].getComment() != null && !cols[j].getComment().isEmpty()) {
                colComment = cols[j].getComment();
            }
            else {
                colComment = cols[j].getName();
            }

            colCommentBuf.append("comment on column ")
                    .append(tabName)
                    .append(".")
                    .append(cols[j].getCode())
                    .append(" is '")
                    .append(colComment)
                    .append("';\n");

            lismsg.add(colCommentBuf.toString());
        }

        return lismsg;
    }

}
