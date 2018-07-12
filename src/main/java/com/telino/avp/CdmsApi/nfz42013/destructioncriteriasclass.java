package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class destructioncriteriasclass extends TypeJAV {

    public destructioncriteriasclass () {
        super ();
        initialize ();
    }

    public destructioncriteriasclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer destructioncriteriaid;
        public String destructioncriteria;
        public Integer mindestructiondelay;
        public final String F = "S005A100E005";
    }
    protected T destructioncriterias_T = new T();

    protected class CLE {
        public Integer destructioncriteriaid;
        public final String F = "S005";
    }
    protected CLE destructioncriterias_CLE = new CLE();

    protected class PKEY {
        public Integer destructioncriteriaid;
        public final String F = "S005";
    }
    protected PKEY destructioncriterias_PKEY = new PKEY();

    public  Integer getDestructioncriteriaid() {
        return (Integer) CurrentObjectValues.get("destructioncriteriaid");
    }

    public void setDestructioncriteriaid(Integer val) {
        setObjectValue (val, "destructioncriteriaid");
    }

    public  String getDestructioncriteria() {
        return (String) CurrentObjectValues.get("destructioncriteria");
    }

    public void setDestructioncriteria(String val) {
        setObjectValue (val, "destructioncriteria");
    }

    public  Integer getMindestructiondelay() {
        return (Integer) CurrentObjectValues.get("mindestructiondelay");
    }

    public void setMindestructiondelay(Integer val) {
        setObjectValue (val, "mindestructiondelay");
    }

    private void initialize () {
        CODOBJ = "destructioncriterias";
        setObjectValue("destructioncriterias","codobj");
        T = destructioncriterias_T;
        CLE = destructioncriterias_CLE;
        PKEY = destructioncriterias_PKEY;
        DUPLICATE = true;
    }
}
