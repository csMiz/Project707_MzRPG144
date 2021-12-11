package com.mizfrank.mzrpg144.item.MzItemWeapon.MzMagic;

public class MzMagicRtObject {

    /**
     * 1-int 2-float 3-byte 4-string
     * */
    public int RtObjType;

    public int Size;

    public byte[] CustomValue;
    public String StringValue;
    public Integer IntValue;
    public Float FloatValue;

    public MzMagicRtObject(){
    }
    public String printType(){
        if (RtObjType == 1){

        }
        else if (RtObjType == 4){
            return "string";
        }
        return "var";
    }
    public String getString(){
        return StringValue;
    }

    public static MzMagicRtObject TypeDelegate(String functionName){
        MzMagicRtObject tmp = new MzMagicRtObject();
        tmp.StringValue = functionName;
        tmp.Size = 4;
        tmp.RtObjType = 4;
        return tmp;
    }
    public static MzMagicRtObject TypeInt(int value){
        MzMagicRtObject tmp = new MzMagicRtObject();
        tmp.IntValue = value;
        tmp.Size = 4;
        tmp.RtObjType = 1;
        return tmp;
    }
    public static MzMagicRtObject TypeFloat(float value){
        MzMagicRtObject tmp = new MzMagicRtObject();
        tmp.FloatValue = value;
        tmp.Size = 4;
        tmp.RtObjType = 2;
        return tmp;
    }

    public static MzMagicRtObject From(String typeIn){
        if (typeIn.equals("integer")){
            return MzMagicRtObject.TypeInt(0);
        }
        return MzMagicRtObject.TypeInt(0);
    }


}
