package org.csnowfox.maven.plugin.model2mybatis.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.csnowfox.maven.plugin.model2mybatis.entity.Column;
import org.csnowfox.maven.plugin.model2mybatis.entity.Key;
import org.csnowfox.maven.plugin.model2mybatis.entity.Table;
import org.csnowfox.maven.plugin.model2mybatis.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: DDLParser
 * @Description Ddl file parser
 * @Author Csnowfox
 * @Date 2019/4/28 23:52
 **/
public class DDLParser implements TableParser {

    @Override
    public List<Table> getTables(String path, String[] tablenames) throws IOException {

        byte[] ddl = Files.readAllBytes(new File(path).toPath());

        List<Table> tabs = new ArrayList<Table>();

        MySqlStatementParser parser = new MySqlStatementParser(new String(ddl));
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement: statementList) {
            if (statement instanceof MySqlCreateTableStatement) {
                MySqlCreateTableStatement table = (MySqlCreateTableStatement) statement;

                Table addTable = new Table();
                addTable.setTableName(((SQLPropertyExpr)table.getTableSource().getExpr()).getName());
                addTable.setComment(table.getComment() == null ? "" : table.getComment().toString());
                addTable.setTableCode(((SQLPropertyExpr)table.getTableSource().getExpr()).getName());
                User user = new User();
                user.setName(((SQLPropertyExpr)table.getTableSource().getExpr()).getOwner().toString());
                user.setCode(((SQLPropertyExpr)table.getTableSource().getExpr()).getOwner().toString());
                addTable.setUser(user);
                List<Column> colList = new LinkedList<>();
                List<Key> keyList = new LinkedList<>();

                List<SQLTableElement> cloumns = table.getTableElementList();
                for (SQLTableElement e : cloumns) {
                    if (e instanceof SQLColumnDefinition) {
                        System.out.println(((SQLColumnDefinition) e).getName().getSimpleName() + "," +((SQLColumnDefinition) e).getDbType() + ","
                                + ((SQLColumnDefinition) e).getDataType() + "," + ((SQLColumnDefinition) e).isPrimaryKey());
                        Column col = new Column();
                        col.setCode(((SQLColumnDefinition) e).getNameAsString());
                        col.setName(((SQLColumnDefinition) e).getNameAsString());
                        col.setType(((SQLColumnDefinition) e).getDataType().toString());
                        col.setComment(((SQLColumnDefinition) e).getComment() == null ? "" : ((SQLColumnDefinition) e).getComment().toString());
                        col.setId(((SQLColumnDefinition) e).getNameAsString());
                        colList.add(col);

                        // The primary key is defined in the field
                        if (((SQLColumnDefinition) e).isPrimaryKey()) {
                            Key primryKey = null;
                            if (keyList.size() != 0) {
                                for (Key k : addTable.getKeys()) {
                                    if (k.getPkFlag().equals(true)) {
                                        primryKey = k;
                                        break;
                                    }
                                }
                                if (primryKey != null) {
                                    String[] newColumns = Arrays.copyOf(primryKey.getColumnId(), primryKey.getColumnId().length + 1);
                                    System.arraycopy(new String[]{((SQLColumnDefinition) e).getNameAsString()}, 0, newColumns, primryKey.getColumnId().length, 1);
                                    primryKey.setColumnId(newColumns);
                                }
                            } else {
                                Key key = new Key();
                                key.setPkFlag(true);
                                key.setColumnId(new String[] {((SQLColumnDefinition) e).getNameAsString()});
                                keyList.add(key);
                            }
                        }
                    }
                    // The primary key definition is a separate statement
                    if (e instanceof MySqlPrimaryKey) {
                        List<SQLSelectOrderByItem> keys = ((MySqlPrimaryKey) e).getColumns();
                        Key key = new Key();
                        String[] keyColumns = new String[keys.size()];
                        int i = 0;
                        for (SQLSelectOrderByItem item : keys) {
                            keyColumns[i++] = ((MySqlPrimaryKey) e).getColumns().get(0).getExpr().toString();
                        }
                        key.setColumnId(keyColumns);
                        key.setPkFlag(true);
                        keyList.add(key);
                    }
                }


                addTable.setCols(colList.toArray(new Column[0]));
                addTable.setKeys(keyList.toArray(new Key[0]));

                if (tablenames == null || tablenames.length <= 0) {
                    tabs.add(addTable);
                } else {
                    for (int i = 0; i < tablenames.length; i++) {
                        if (tablenames[i].toUpperCase().equals(addTable.getTableName().toUpperCase())) {
                            tabs.add(addTable);
                        }
                    }
                }

            }
        }

        return tabs;
    }

}
