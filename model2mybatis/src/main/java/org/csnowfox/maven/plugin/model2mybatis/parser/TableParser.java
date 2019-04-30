package org.csnowfox.maven.plugin.model2mybatis.parser;

import org.csnowfox.maven.plugin.model2mybatis.entity.Table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @InterfaceName: TableParser
 * @Description Parser interface
 * @Author Csnowfox
 * @Date 2019/4/28 23:53
 **/
public interface TableParser {

    public List<Table> getTables(String path, String[] tablenames) throws IOException;

}
