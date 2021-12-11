package com.mizfrank.mzrpg144.item.MzItemWeapon.MzMagic;

import javafx.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.*;

public class MzMagicRuntime {

    /**
     * 代码区
     * */
    protected List<String> SegmentCode = new ArrayList<>();

    /**
     * 常量区
     * */
    protected Map<String, MzMagicRtObject> SegmentConstant = new HashMap<>();

    /**
     * 全局区
     * */
    protected Map<String, MzMagicRtObject> SegmentGlobal = new HashMap<>();

    /**
     * 堆区
     * */
    protected Map<String, MzMagicRtObject> SegmentHeap = new HashMap<>();

    /**
     * 栈区
     * */
    protected Stack<Pair<String, MzMagicRtObject>> SegmentStack = new Stack<>();

    /**
     * 0-结束 1-运行中
     * */
    protected int FlagExit = 1;

    protected Map<String, Integer> StructureInfoPosition = new HashMap<>();

    protected Map<String, Integer> FunctionInfoPosition = new HashMap<>();

    protected Map<String, String[]> FunctionArgType = new HashMap<>();

    protected int RunningCursor = 0;


    public int loadExe(ItemStack exeIn){
        CompoundNBT nbt = exeIn.getOrCreateTag();
        ListNBT list = nbt.getList("lines",8);
        String currentStruct = ".";
        for(int i = 0; i < list.size(); ++i) {
            String tmpStr = list.getString(i);
            SegmentCode.add(tmpStr);
            String[] segs = tmpStr.split(" ");
            if (segs.length > 1){
                String cmd = segs[0];
                String cmd2 = segs[1];
                if (cmd.equals("structure")){
                    if (currentStruct.length() == 1){
                        currentStruct = cmd2 + ".";
                        if (StructureInfoPosition.containsKey(cmd2)){
                            return 1;
                        }
                        StructureInfoPosition.put(cmd2, i);
                    } else{
                        return 1;
                    }
                }
                else if (cmd.equals("function") || cmd.equals("sub")){
                    String tmpKey = currentStruct + cmd2;
                    if (FunctionInfoPosition.containsKey(tmpKey)){
                        return 1;
                    }
                    FunctionInfoPosition.put(tmpKey, i);
                    // TODO: FunctionArgType
                }
                else if (cmd.equals("end")){
                    if (cmd2.equals("structure")){
                        if (currentStruct.length() > 1){
                            currentStruct = ".";
                        }
                        else{
                            return 1;
                        }
                    }
                }
            }
        }
        if (FunctionInfoPosition.containsKey(".main")){
            SegmentStack.push(new Pair<>("sub", MzMagicRtObject.TypeInt(-1)));
            SegmentStack.push(new Pair<>("sub", MzMagicRtObject.TypeDelegate(".main")));
            RunningCursor = FunctionInfoPosition.get(".main");

            return 0;
        }
        return 1;
    }

    public boolean isNormalEnd(){
        return (FlagExit == 0);
    }

    public float getAbortPenalty(){
        int memoryUse = 0;
        memoryUse += getSegmentCodeSize();
        memoryUse += getSegmentConstantSize();
        memoryUse += getSegmentGlobalSize();
        memoryUse += getSegmentHeapSize();
        memoryUse += getSegmentStackSize();
        return (memoryUse/16.0f);
    }

    /**
     * 单步执行，正常返回0，其它返回值表示运行异常
     * */
    public int runLine(){
        String line = SegmentCode.get(RunningCursor);
        String[] segs = line.split(" ");
        if (segs.length > 0){
            String cmd = segs[0];
            if (cmd.equals("sub")){
                // TODO: 校验参数类型及数量
                Stack<MzMagicRtObject> args = new Stack<>();
                while (!SegmentStack.peek().getKey().equals("sub")){
                    args.push(SegmentStack.pop().getValue());
                }
                String funcDefName = SegmentStack.peek().getValue().getString();
                String[] argDefList = FunctionArgType.get(funcDefName);
                if (args.size()*2 == argDefList.length){
                    if (argDefList.length > 0){
                        for (int i = 0; i < args.size(); i++){
                            if (argDefList[i*2].equals(args.peek().printType())){
                                SegmentStack.push(new Pair<>(argDefList[i*2+1], args.pop()));
                            }
                            else{
                                return 1;
                            }
                        }
                    }
                }
                else{
                    return 1;
                }
            }
            else if (cmd.equals("dim")){
                if (segs.length == 4){
                    String varName = segs[1];
                    if (!segs[2].equals("as")){
                        return 1;
                    }
                    String varType = segs[3];
                    SegmentStack.push(new Pair<>(varName, MzMagicRtObject.From(varType)));
                }
                else{
                    return 1;
                }
            }
            else{
                if (segs.length > 3){
                    if (segs[1].equals("=")){  // 赋值
                        String tmpVarName = segs[0];
                        List<String> tmpExpr = new ArrayList<>();
                        for (int i = 2; i < segs.length; i++){
                            tmpExpr.add(segs[i]);
                        }
                        MzMagicRtObject targetObj = findStackVariable(tmpVarName);
                        if (!SegmentStack.peek().getKey().equals("expr")){
                            String exprSeg = tmpExpr.get(0);
                            if (isNumber(exprSeg)){
                                SegmentStack.push(new Pair<>("expr", parseNumber(exprSeg)));
                            }
                            else if (isOperator(exprSeg)){

                            }
                        }
                    }
                }
            }

        }
        RunningCursor += 1;
        return 0;
    }

    public MzMagicRtObject findStackVariable(String varName){
        // TODO
        return null;
    }

    public boolean isNumber(String expr){
        char[] chrs = expr.toCharArray();
        int dotCount = 0;
        for (int i = 0; i < chrs.length; i++){
            char ch = chrs[i];
            if (ch >= '0' && ch <= '9'){
                // pass
            }
            else if (ch == '.'){
                dotCount += 1;
            }
            else{
                if (i == chrs.length - 1){
                    if (ch=='f'||ch=='F'||ch=='d'||ch=='D'){
                        // pass
                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
        }
        return (dotCount <= 1);
    }
    public MzMagicRtObject parseNumber(String expr){
        char[] chrs = expr.toCharArray();
        int dotCount = 0;
        for (char ch : chrs){
            if (ch == '.'){
                dotCount += 1;
            }
        }
        if (dotCount == 1){
            if (chrs[chrs.length-1] == 'f' || chrs[chrs.length-1] == 'F'){
                return MzMagicRtObject.TypeFloat(Float.parseFloat(expr));
            }
            else{
                //return MzMagicRtObject.TypeDouble(expr);
            }
        }
        return MzMagicRtObject.TypeInt(Integer.parseInt(expr));
    }
    public boolean isOperator(String expr){
        if (expr.equals("+")){
            return true;
        }
        else if (expr.equals("-")){
            return true;
        }
        else if (expr.equals("*")){
            return true;
        }
        else if (expr.equals("/")){
            return true;
        }
        else if (expr.equals("%")){
            return true;
        }
        return false;
    }

    public int getSegmentCodeSize(){
        int result = 0;
        for (String line : SegmentCode){
            result += line.length();
        }
        return result;
    }
    public int getSegmentConstantSize(){
        int result = 0;
        for (MzMagicRtObject obj : SegmentConstant.values()){
            result += obj.Size;
        }
        return result;
    }
    public int getSegmentGlobalSize(){
        int result = 0;
        for (MzMagicRtObject obj : SegmentGlobal.values()){
            result += obj.Size;
        }
        return result;
    }
    public int getSegmentHeapSize(){
        int result = 0;
        for (MzMagicRtObject obj : SegmentHeap.values()){
            result += obj.Size;
        }
        return result;
    }
    public int getSegmentStackSize(){
        int result = 0;
        for (Pair<String, MzMagicRtObject> pair : SegmentStack){
            result += pair.getValue().Size;
        }
        return result;
    }
}

