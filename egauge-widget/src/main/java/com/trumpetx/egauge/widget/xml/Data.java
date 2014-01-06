package com.trumpetx.egauge.widget.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/*
    <data serial="0x43eace1a">
        <ts>1388429246</ts>
        <gen>265027</gen>
        <r t="P" n="Grid">
            <v>13107803214</v>
            <i>358</i>
        </r>
        <r t="P" n="Solar">
            <v>6432723822</v>
            <i>1278</i>
        </r>
        <r t="P" n="Solar+">
            <v>6454418265</v>
            <i>1278</i>
        </r>
    </data>
 */
@Root(name = "data")
public class Data {
    @Attribute
    private String serial;

    @Element(name = "ts")
    private long timestamp;

    @Element
    private int gen;

    @ElementList(inline = true)
    private List<Register> registers;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getGen() {
        return gen;
    }

    public void setGen(int gen) {
        this.gen = gen;
    }

    public List<Register> getRegisters() {
        return registers;
    }

    public void setRegisters(List<Register> registers) {
        this.registers = registers;
    }
}
