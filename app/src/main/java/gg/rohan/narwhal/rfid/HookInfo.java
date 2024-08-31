package gg.rohan.narwhal.rfid;

import java.util.function.BiConsumer;

class HookInfo {

    private final int what;
    private final int arg1;
    private final boolean requireTriggerPressed;
    private final BiConsumer<Integer, Object> hook;

    public HookInfo(int what, int arg1, boolean requireTriggerPressed, BiConsumer<Integer, Object> hook) {
        this.what = what;
        this.arg1 = arg1;
        this.requireTriggerPressed = requireTriggerPressed;
        this.hook = hook;
    }

    public int getWhat() {
        return what;
    }

    public int getArg1() {
        return arg1;
    }

    public boolean isRequireTriggerPressed() {
        return requireTriggerPressed;
    }

    public BiConsumer<Integer, Object> getHook() {
        return hook;
    }

}
