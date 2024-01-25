package de.ravensnight.fireknight.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamReader {

    public static class ParamConfig {
        private final Object key;
        private Character shortKey;
        private String longKey;
        private final boolean flag;
        private boolean required = false;
        private String description;

        ParamConfig(Object key, boolean flag) {
            this.flag = flag;
            this.key = key;
        }

        public boolean isFlag() {
            return this.flag;
        }

        public Object getKey() {
            return this.key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Character getShortKey() {
            return shortKey;
        }

        public void setShortKey(Character shortKey) {
            this.shortKey = shortKey;
        }

        public String getLongKey() {
            return longKey;
        }

        public void setLongKey(String longKey) {
            this.longKey = longKey;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        
    }

    private final Map<Character, ParamConfig> shortKeys = new HashMap<>();
    private final Map<String, ParamConfig> longKeys = new HashMap<>();
    private final Set<Object> required = new HashSet<>();
    private final ParamHandler receiver;

    private static Pattern shortPattern = Pattern.compile("^-([a-z,A-Z])");
    private static Pattern longPattern = Pattern.compile("^--([a-z,A-Z][a-z,A-Z,0-9]*)");

    public ParamReader(ParamHandler receiver) {
        this.receiver = receiver;
    }

    public ParamBuilder bind(Object key) {
        return new ParamBuilder(key, this);
    }

    void register(Object key, Character shortKey, String longKey, boolean flag, boolean required, String desc) {
        ParamConfig conf = new ParamConfig(key, flag);
        conf.setShortKey(shortKey);
        conf.setLongKey(longKey);
        conf.setDescription(desc);
        conf.setRequired(required);

        if (shortKey != null) {
            this.shortKeys.put(shortKey, conf);
        }

        if (longKey != null) {
            this.longKeys.put(longKey, conf);
        }

        if (required) {
            this.required.add(key);
        }
    }

    public Collection<ParamConfig> getParameters() {
        Map<Object, ParamConfig> result = new HashMap<>();

        for (ParamConfig c : this.shortKeys.values()) {
            result.put(c.getKey(), c);
        }

        for (ParamConfig c : this.longKeys.values()) {
            if (result.containsKey(c.getKey())) continue;            
            result.put(c.getKey(), c);
        }

        return result.values();
    }

    public void parse(String[] params) throws ParamReaderException {
        if (params == null) return;
        
        List<String> source = new ArrayList<>();
        source.addAll(Arrays.asList(params));

        Set<Object> needed = new HashSet<>();
        needed.addAll(this.required);

        ParamConfig parameterConfig = null;

        while (!source.isEmpty()) {
            String entry = source.remove(0).trim();

            if (parameterConfig != null) {
                this.receiver.handleParam(parameterConfig.getKey(), entry);
                parameterConfig = null;
            } else {
                String k = null;                
                Matcher m = longPattern.matcher(entry);
                ParamConfig conf = null;

                if (m.matches()) {
                    k = m.group(1);
                    conf = this.longKeys.get(k);

                    if (conf != null) {
                        if (conf.isFlag()) {
                            this.receiver.handleFlag(conf.getKey());
                        } else {
                            parameterConfig = conf;
                        }

                        if (needed.contains(conf.getKey())) {
                            needed.remove(conf.getKey());
                        }
                    }
                } 
                else {
                    m = shortPattern.matcher(entry);
                    if (m.matches()) {
                        k = m.group(1);
                        conf = this.shortKeys.get(k.charAt(0));

                        if (conf != null) {
                            if (conf.isFlag()) {
                                this.receiver.handleFlag(conf.getKey());
                            } else {
                                parameterConfig = conf;
                            }

                            if (needed.contains(conf.getKey())) {
                                needed.remove(conf.getKey());
                            }
                        }
                    }
                }
            }
        }

        if (needed.size() > 0) {
            throw new ParamReaderException("Required params are missing: " + String.valueOf(needed));
        }
    }
}
