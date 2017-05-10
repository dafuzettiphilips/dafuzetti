package com.philips.websuite.infrastructure.database;

import com.google.inject.Inject;
import com.philips.app.commons.log.PhilipsLogger;
import com.philips.app.data.ds.DataSourceProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.integration.QueryConnectionReceiver;

/**
 * @author crhobus
 */
public class DatabaseProvider implements ConnectionProvider {

    public static final String JDBC_CONTEXT_CONNECTION_TASY = "jdbc/TASY";
    public static final String JAVA_COMP_ENV = "java:comp/env";

    @Inject
    private DataSourceProvider dataSource;

    @Inject
    private InitialContext initialContext;

    private Optional<String> databaseName = Optional.empty();

    @Override
    public void provide(final QueryConnectionReceiver query) throws SQLException {
        query.receive(getConnection());
    }

    public Connection getConnection() throws SQLException {
        try {
            Context envCtx = (Context) initialContext.lookup(JAVA_COMP_ENV);
            DataSource ds = (DataSource) envCtx.lookup(JDBC_CONTEXT_CONNECTION_TASY);
            return ds.getConnection();
        } catch (NoInitialContextException e) {
            return getConnectionFromGuiceStart();
        } catch (NamingException e) {
            throw new SQLException(e);
        }
    }

    private Connection getConnectionFromGuiceStart() {
        try {
            final String name = getFirstDatabase();
            final DataSource ds = dataSource.getDataSoure(name);
            return ds.getConnection();
        } catch (Exception e) {
            PhilipsLogger.getLogger(DatabaseProvider.class).error("Error when get connection from GuiceStart");
        }
        return null;
    }

    private String getFirstDatabase() throws NamingException {
        if (!databaseName.isPresent()) {
            databaseName = dataSource.getAvailableDataSources().stream().findFirst();
        }
        return databaseName.orElse(null);
    }
}
