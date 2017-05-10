package com.philips.websuite.infrastructure.database;

import com.google.common.base.Joiner;
import com.philips.app.data.DataFunction;
import com.philips.app.data.DataQuery;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * @author crhobus
 */
public class TasyDataProcedure {

    private final DataQuery data;
    private final String procedureName;
    private final List<TasyProcedureValues> values = new ArrayList<>();

    /**
     * Default constructor.
     *
     * @param procedureName set the procedure name
     * @param data set the query manager
     */
    public TasyDataProcedure(final String procedureName, final DataQuery data) {
        this.procedureName = procedureName;
        this.data = data;
    }

    /**
     * Configure procedure parameter.
     *
     * @param value of procedure parameter
     * @return this instance
     */
    private TasyDataProcedure addValue(final TasyProcedureValues value) {
        value.setPosition(values.size() + 1);
        values.add(value);
        return this;
    }

    /**
     * Configure value to be registered as input.
     *
     * @param value define the input value
     * @return this instance
     */
    public TasyDataProcedure in(final Object value) {
        return addValue(new TasyProcedureValues(null, value, TasyProcedureType.IN));
    }

    /**
     * Configure value to be registered as output.
     *
     * @param name define the column name of procedure result
     * @return this instance
     */
    public TasyDataProcedure out(final String name) {
        return addValue(new TasyProcedureValues(name, null, TasyProcedureType.OUT));
    }

    /**
     * Configure value to be registered as output.
     *
     * @param name define the column name of procedure result
     * @param returnType define the column type of procedure result
     * @return this instance
     */
    public TasyDataProcedure out(final String name, final TasyProcedureParam returnType) {
        final TasyProcedureValues tpv = new TasyProcedureValues(name, null, TasyProcedureType.OUT);
        tpv.setParam(returnType);
        return addValue(tpv);
    }

    /**
     * Configure value to be registered as input and output.
     *
     * @param name define the column name of procedure result
     * @param value define the input value
     * @return this instance
     */
    public TasyDataProcedure inOut(final String name, final Object value) {
        return addValue(new TasyProcedureValues(name, value, TasyProcedureType.IN_OUT));
    }

    /**
     * Execute the procedure.
     *
     * @return the values of columns define as output
     */
    public Map<String, Object> execute() {
        return execute(mp -> mp);
    }

    /**
     * Execute the procedure.
     *
     * @param mapper define a result mapper
     * @param <T> define the object type to return
     * @return the object define by mapper
     */
    public <T> T execute(final DataFunction<Map<String, Object>, T> mapper) {
        return data.callable(String.format("{call %s }", prepareProcedureName()), callStatment -> {
            for (TasyProcedureValues value : values) {
                final int sqlType = value.getParamSQL();
                if (TasyProcedureType.isOut(value.getType())) {
                    callStatment.registerOutParameter(value.getPosition(), sqlType);
                }
                if (TasyProcedureType.isIn(value.getType())) {
                    if (sqlType == Types.TIMESTAMP) {
                        callStatment.setTimestamp(value.getPosition(), (Timestamp) value.getValue());
                    } else {
                        callStatment.setObject(value.getPosition(), value.getValue());
                    }
                }
            }

            callStatment.execute();

            final Map<String, Object> result = new HashMap<>();
            for (TasyProcedureValues value : values) {
                if (TasyProcedureType.isOut(value.getType())) {
                    result.put(value.getName(), callStatment.getObject(value.getPosition()));
                }
            }

            return mapper.execute(result);
        });
    }

    /**
     * Configured the sql procedure instruction.
     *
     * @return the sql command
     */
    private String prepareProcedureName() {
        final List<String> values = new ArrayList<>();
        this.values.forEach(fe -> values.add("?"));
        final StringBuilder sb = new StringBuilder(procedureName).append(" ");
        if (!values.isEmpty()) {
            sb.append('(').append(Joiner.on(",").join(values.iterator())).append(')');
        }
        return sb.toString();
    }

    /**
     * Define the type value from input and output values.
     */
    public enum TasyProcedureParam {
        TIMESTAMP, INTEGER, DOUBLE, FLOAT, VARCHAR
    }

    /**
     * Define how the values are configured in procedure calls.
     */
    private enum TasyProcedureType {

        IN, OUT, IN_OUT;

        /**
         * Indicate if parameter is output.
         *
         * @param type set the configured type
         * @return <code>true</code> if is output parameter
         */
        private static boolean isOut(final TasyProcedureType type) {
            return OUT.equals(type) || IN_OUT.equals(type);
        }

        /**
         * Indicate if parameter is input.
         *
         * @param type set the configured type
         * @return <code>true</code> if is input parameter
         */
        private static boolean isIn(final TasyProcedureType type) {
            return IN.equals(type) || IN_OUT.equals(type);
        }
    }

    /**
     * Define the all properties from procedure parameter.
     */
    private static final class TasyProcedureValues {

        private final String name;
        private final Object value;
        private final TasyProcedureType type;
        private TasyProcedureParam param;
        private int position;

        /**
         * Default constructor.
         *
         * @param name define the column name of procedure result
         * @param value define the input value
         * @param type define the column type of procedure result
         */
        TasyProcedureValues(final String name, final Object value, final TasyProcedureType type) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.param = configParam(value);
        }

        /**
         * Define the column type result.
         *
         * @param param set the type result
         */
        public void setParam(final TasyProcedureParam param) {
            this.param = param;
        }

        /**
         * Define the position of parameter.
         *
         * @param position of procedure parameter
         */
        public void setPosition(final int position) {
            this.position = position;
        }

        /**
         * Provide the column name.
         *
         * @return the column name or <code>null</code>
         */
        public String getName() {
            return name;
        }

        /**
         * Provide the input value.
         *
         * @return the input value or <code>null</code>
         */
        public Object getValue() {
            return value;
        }

        /**
         * Provide the type of parameter.
         *
         * @return the type of parameter
         */
        public TasyProcedureType getType() {
            return type;
        }

        /**
         * Provide the type of value.
         *
         * @return the type of value
         */
        public TasyProcedureParam getParam() {
            return param;
        }

        public int getPosition() {
            return position;
        }

        private TasyProcedureParam configParam(final Object valor) {
            TasyProcedureParam param = TasyProcedureParam.VARCHAR;
            if (valor instanceof Timestamp) {
                param = TasyProcedureParam.TIMESTAMP;
            } else if (valor instanceof Integer) {
                param = TasyProcedureParam.INTEGER;
            } else if (valor instanceof Double) {
                param = TasyProcedureParam.DOUBLE;
            } else if (valor instanceof Float) {
                param = TasyProcedureParam.FLOAT;
            }
            return param;
        }

        private int getParamSQL() {
            final int value;
            switch (Optional.ofNullable(param).orElse(TasyProcedureParam.VARCHAR)) {
                case TIMESTAMP:
                    value = Types.TIMESTAMP;
                    break;
                case INTEGER:
                    value = Types.INTEGER;
                    break;
                case DOUBLE:
                    value = Types.DOUBLE;
                    break;
                case FLOAT:
                    value = Types.FLOAT;
                    break;
                default:
                    value = Types.VARCHAR;
                    break;
            }
            return value;
        }
    }

}
