package au.com.mithril.rpgtalker;

import java.util.Properties;

public class RobProperties extends Properties {
    public boolean readBoolean(String key, boolean adefault) {
        if (containsKey(key)) {
            return Boolean.parseBoolean(getProperty(key));
        } else {
            return adefault;
        }
    }

    public void writeBoolean( String key, boolean value) {
        setProperty(key,String.valueOf(value));
    }
}