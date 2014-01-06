package com.trumpetx.egauge.widget.xml;

import com.google.common.collect.ImmutableMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root(name = "r")
public class Register {

    public static final Map<String, String> REGISTER_TYPE_LABELS;

    static {
        REGISTER_TYPE_LABELS = new ImmutableMap.Builder<String, String>()
                .put("Ee", "W/m^2")
                .put("F", "mHz")
                .put("I", "mA")
                .put("PQ", "var")
                .put("Pa", "Pa")
                .put("P", "W")
                .put("Qv", "mm^3/s")
                .put("Q", "g/s")
                .put("R", "\u2126")
                .put("S", "VA")
                .put("THD", "ms(10^3 * s)")
                .put("T", "mC")
                .put("V", "mV")
                .put("#", "")
                .put("$", "")
                .put("a", "m\u00B0")
                .put("h", "0.1%")
                .put("v", "m/s")
                .build();
    }

    @Attribute(name = "t")
    private String type;

    @Attribute(name = "n")
    private String name;

    @Element(name = "v")
    private long value;

    @Element(name = "i", required = false)
    private long rateOfChange;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getRateOfChange() {
        return rateOfChange;
    }

    public void setRateOfChange(long rateOfChange) {
        this.rateOfChange = rateOfChange;
    }
}
