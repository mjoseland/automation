package name.joseland.mal.automation.requestrepo.db.config;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.dialect.PostgreSQL95Dialect;

import java.sql.Types;

public class PostgreSQL95DialectExtended extends PostgreSQL95Dialect {

    public PostgreSQL95DialectExtended() {
        super();
        this.registerHibernateType(Types.OTHER, JsonNodeBinaryType.class.getName());
    }

}
