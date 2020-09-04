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
                SegmentStack.push(new Pair<>(segs[1], new MzMagicRtObject()));

            }

        }
        RunningCursor += 1;
        return 0;
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

