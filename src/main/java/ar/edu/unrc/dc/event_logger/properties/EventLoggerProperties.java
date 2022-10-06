package ar.edu.unrc.dc.event_logger.properties;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static ar.edu.unrc.dc.event_logger.properties.EventLoggerProperties.ConfigKey.*;

public class EventLoggerProperties {

    private static final String EVENT_LOGGER_PREFIX = "event_logger";
    private static final NamingConvention NAMING_CONVENTION_DEFAULT = NamingConvention.AS_IS;
    private static final int RMI_PORT_DEFAULT = 0;
    private static final int RMI_REGISTRY_PORT_DEFAULT = 1099;
    private static final String RMI_SERVER_URL_DEFAULT = "127.0.0.1";
    private static final int RMI_CLIENT_CONNECTION_RETRIES_DEFAULT = 5;
    private static final long RMI_CLIENT_CONNECTION_RETRY_DELAY_DEFAULT = 2;
    private static final boolean PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE_DEFAULT = true;

    private static final String RMI_CLIENT_SECURITY_POLICY_DEFAULT = "src/main/resources/eventloggerclient.policy";

    private static final String RMI_SERVER_SECURITY_POLICY_DEFAULT = "src/main/resources/eventloggerserver.policy";

    private static final String MAIN_EVENT_DEFAULT_NAME = "MAIN";

    private static boolean properties_use_default_on_invalid_value = Boolean.parseBoolean(
            getPropertyValue(
                    EVENT_LOGGER_PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE,
                    Boolean.toString(PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE_DEFAULT
                    )
            )
    );

    public enum NamingConvention {
        AS_IS,
        LOWERCASE,
        UPPERCASE
    }

    public enum ConfigKey {
        EVENT_LOGGER_PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE {
            @Override
            public String getKey() {
                return EVENT_LOGGER_PREFIX + ".properties.use_default_on_invalid_value";
            }

            @Override
            public String getDescription() {
                return "If a property's value is not valid, the default value will be used instead. Default value is " + PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE_DEFAULT;
            }
        },
        EVENT_LOGGER_NAMING_CONVENTION {
            @Override
            public String getKey() {
                return EVENT_LOGGER_PREFIX + ".naming_convention";
            }

            @Override
            public String getDescription() {
                return "Naming convention used for event names. Possible values are " + Arrays.toString(NamingConvention.values()) + ". Default value is " + NAMING_CONVENTION_DEFAULT;
            }
        },
        EVENT_LOGGER_RMI_PORT {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.port"; }

            @Override
            public String getDescription() {
                return "Default port number used for RMI exported remote objects, default is " + RMI_PORT_DEFAULT + " (a 0 value means that a random port will be used)";
            }
        },
        EVENT_LOGGER_RMI_REGISTRY_PORT {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi_registry.port"; }

            @Override
            public String getDescription() {
                return "Default port number used for the RMI registry, default is " + RMI_REGISTRY_PORT_DEFAULT;
            }
        },
        EVENT_LOGGER_SERVER_URL {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.url"; }

            @Override
            public String getDescription() {
                return "The URL to the server, default value is " + RMI_SERVER_URL_DEFAULT;
            }
        },
        EVENT_LOGGER_CLIENT_CONNECTION_RETRIES {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.client.connection.retries"; }

            @Override
            public String getDescription() {
                return "How many retries will be made to connect to the server, default value is " + RMI_CLIENT_CONNECTION_RETRIES_DEFAULT;
            }
        },
        EVENT_LOGGER_CLIENT_CONNECTION_RETRY_DELAY {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.client.connection.retry_delay"; }

            @Override
            public String getDescription() {
                return "Delay in seconds to retry a connection to the server, default value is " + RMI_CLIENT_CONNECTION_RETRY_DELAY_DEFAULT + "s";
            }
        },
        EVENT_LOGGER_MAIN_EVENT_DEFAULT_NAME {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.server.main_event_default_name"; }

            @Override
            public String getDescription() {
                return "The default main event name, default value is " + MAIN_EVENT_DEFAULT_NAME;
            }
        },
        EVENT_LOGGER_RMI_CLIENT_POLICY {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.client.policy"; }

            @Override
            public String getDescription() {
                return "The policy file to use by the client, default value is " + RMI_CLIENT_SECURITY_POLICY_DEFAULT;
            }
        },
        EVENT_LOGGER_RMI_SERVER_POLICY {
            @Override
            public String getKey() { return EVENT_LOGGER_PREFIX + ".rmi.server.policy"; }

            @Override
            public String getDescription() {
                return "The policy file to use by the server, default value is " + RMI_SERVER_SECURITY_POLICY_DEFAULT;
            }
        }
        ;
        public abstract String getKey();
        public abstract String getDescription();
    }

    public static NamingConvention namingConvention() {
        String namingConventionValue = getPropertyValue(EVENT_LOGGER_NAMING_CONVENTION, NAMING_CONVENTION_DEFAULT.toString());
        return NamingConvention.valueOf(stringPropertyValue(EVENT_LOGGER_NAMING_CONVENTION, namingConventionValue, NAMING_CONVENTION_DEFAULT.toString()));
    }

    public static String serverURL() {
        String serverURL = getPropertyValue(EVENT_LOGGER_SERVER_URL, RMI_SERVER_URL_DEFAULT);
        return stringPropertyValue(EVENT_LOGGER_SERVER_URL, serverURL, RMI_SERVER_URL_DEFAULT);
    }

    public static String defaultMainEventName() {
        String defaultMainEventName = getPropertyValue(EVENT_LOGGER_MAIN_EVENT_DEFAULT_NAME, MAIN_EVENT_DEFAULT_NAME);
        return stringPropertyValue(EVENT_LOGGER_MAIN_EVENT_DEFAULT_NAME, defaultMainEventName, MAIN_EVENT_DEFAULT_NAME);
    }

    public static String clientPolicy() {
        String clientPolicy = getPropertyValue(EVENT_LOGGER_RMI_CLIENT_POLICY, RMI_CLIENT_SECURITY_POLICY_DEFAULT);
        return stringPropertyValue(EVENT_LOGGER_RMI_CLIENT_POLICY, clientPolicy, RMI_CLIENT_SECURITY_POLICY_DEFAULT);
    }

    public static String serverPolicy() {
        String serverPolicy = getPropertyValue(EVENT_LOGGER_RMI_SERVER_POLICY, RMI_SERVER_SECURITY_POLICY_DEFAULT);
        return stringPropertyValue(EVENT_LOGGER_RMI_SERVER_POLICY, serverPolicy, RMI_SERVER_SECURITY_POLICY_DEFAULT);
    }

    public static int port() {
        String portStringValue = getPropertyValue(EVENT_LOGGER_RMI_PORT, String.valueOf(RMI_PORT_DEFAULT));
        return (int) numberPropertyValue(EVENT_LOGGER_RMI_PORT, portStringValue, RMI_PORT_DEFAULT);
    }

    public static int registryPort() {
        String portStringValue = getPropertyValue(EVENT_LOGGER_RMI_REGISTRY_PORT, String.valueOf(RMI_REGISTRY_PORT_DEFAULT));
        return (int) numberPropertyValue(EVENT_LOGGER_RMI_REGISTRY_PORT, portStringValue, RMI_REGISTRY_PORT_DEFAULT);
    }

    public static int connectionRetries() {
        String retriesStringValue = getPropertyValue(EVENT_LOGGER_CLIENT_CONNECTION_RETRIES, String.valueOf(RMI_CLIENT_CONNECTION_RETRIES_DEFAULT));
        return (int) numberPropertyValue(EVENT_LOGGER_CLIENT_CONNECTION_RETRIES, retriesStringValue, RMI_CLIENT_CONNECTION_RETRIES_DEFAULT);
    }

    public static long reconnectionDelay() {
        String reconnectionDelayStringValue = getPropertyValue(EVENT_LOGGER_CLIENT_CONNECTION_RETRY_DELAY, String.valueOf(RMI_CLIENT_CONNECTION_RETRY_DELAY_DEFAULT));
        return numberPropertyValue(EVENT_LOGGER_CLIENT_CONNECTION_RETRY_DELAY, reconnectionDelayStringValue, RMI_CLIENT_CONNECTION_RETRY_DELAY_DEFAULT);
    }

    public static Map<String, String> getOptionsAndDescriptions() {
        Map<String, String> optionsAndDescriptions = new TreeMap<>();
        for (ConfigKey configKey : ConfigKey.values()) {
            optionsAndDescriptions.put(configKey.getKey(), configKey.getDescription());
        }
        return optionsAndDescriptions;
    }

    public static String[] asArgs() {
        String[] args = new String[values().length-1];
        int idx = 0;
        for (ConfigKey key : ConfigKey.values()) {
            if (key.equals(EVENT_LOGGER_PROPERTIES_USE_DEFAULT_ON_INVALID_VALUE))
                continue;
            args[idx++] = "-D" + key.getKey()+"="+getValidPropertyValue(key);
        }
        return args;
    }

    private static String getValidPropertyValue(ConfigKey key) {
        String value;
        boolean previousValue = properties_use_default_on_invalid_value;
        properties_use_default_on_invalid_value = true;
        switch (key) {
            case EVENT_LOGGER_NAMING_CONVENTION : {value = namingConvention().toString(); break; }
            case EVENT_LOGGER_RMI_REGISTRY_PORT : {value = String.valueOf(registryPort()); break; }
            case EVENT_LOGGER_RMI_PORT : {value = String.valueOf(port()); break; }
            case EVENT_LOGGER_SERVER_URL : {value = serverURL(); break; }
            case EVENT_LOGGER_CLIENT_CONNECTION_RETRIES : {value = String.valueOf(connectionRetries()); break; }
            case EVENT_LOGGER_CLIENT_CONNECTION_RETRY_DELAY : {value = String.valueOf(reconnectionDelay()); break; }
            default: throw new IllegalArgumentException("Config key " + key + " is not valid");
        }
        properties_use_default_on_invalid_value = previousValue;
        return value;
    }

    private static long numberPropertyValue(ConfigKey key, String obtainedValue, long defaultValue) {
        if (checkValue(key, obtainedValue)) {
            return toNumber(obtainedValue);
        } else if (properties_use_default_on_invalid_value) {
            return defaultValue;
        } else {
            throwException(key, obtainedValue);
        }
        return 0; //should never reach this statement
    }

    private static String stringPropertyValue(ConfigKey key, String obtainedValue, String defaultValue) {
        if (checkValue(key, obtainedValue)) {
            return obtainedValue;
        } else if (properties_use_default_on_invalid_value) {
            return defaultValue;
        } else {
            throwException(key, obtainedValue);
        }
        return null; //should never reach this statement
    }

    private static void throwException(ConfigKey key, String value) {
        throw new IllegalArgumentException("Invalid value " + value + " for property " + key.getKey());
    }

    private static String getPropertyValue(ConfigKey key, String defaultValue) {
        String propValue = System.getProperty(key.getKey());
        if (propValue == null)
            return defaultValue;
        return propValue;
    }

    private static boolean checkValue(ConfigKey key, String value) {
        switch (key) {
            case EVENT_LOGGER_NAMING_CONVENTION : {
                for (NamingConvention namingConvention : NamingConvention.values()) {
                    if (namingConvention.toString().equalsIgnoreCase(value))
                        return true;
                }
                return false;
            }
            case EVENT_LOGGER_RMI_REGISTRY_PORT :
            case EVENT_LOGGER_RMI_PORT :
            case EVENT_LOGGER_CLIENT_CONNECTION_RETRIES :
            case EVENT_LOGGER_CLIENT_CONNECTION_RETRY_DELAY :
                return isNumber(value);
            case EVENT_LOGGER_SERVER_URL : return !value.isEmpty();
        }
        return false;
    }

    private static int toNumber(String stringRep) {
        return Integer.parseInt(stringRep);
    }

    private static boolean isNumber(String stringRep) {
        try {
            int num = Integer.parseInt(stringRep);
            return num >= 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
