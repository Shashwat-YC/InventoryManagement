package gg.rohan.narwhal.rfid;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

class ReaderHandler extends Handler {

    private final Map<UUID, HookInfo> messageHooks = new LinkedHashMap<>();
    private final Map<UUID, HookInfo> internalHooks = new LinkedHashMap<>();

    @Override
    public void handleMessage(@NonNull Message msg) {
        internalHooks.forEach((uuid, hookInfo) -> {
            if (hookInfo.getWhat() == msg.what && hookInfo.getArg1() == msg.arg1) {
                if (hookInfo.isRequireTriggerPressed() && ReaderStaticWrapper.isTriggerPressed()) {
                    hookInfo.getHook().accept(msg.what, msg.obj);
                } else if (!hookInfo.isRequireTriggerPressed()) {
                    hookInfo.getHook().accept(msg.what, msg.obj);
                }
            }
        });
        messageHooks.forEach((uuid, hookInfo) -> {
            if (hookInfo.getWhat() == msg.what && hookInfo.getArg1() == msg.arg1) {
                if (hookInfo.isRequireTriggerPressed() && ReaderStaticWrapper.isTriggerPressed()) {
                    hookInfo.getHook().accept(msg.what, msg.obj);
                } else if (!hookInfo.isRequireTriggerPressed()) {
                    hookInfo.getHook().accept(msg.what, msg.obj);
                }
            }
        });
    }

    UUID addMessageHook(int what, int arg1, boolean requireTriggerPressed, BiConsumer<Integer, Object> hook) {
        UUID uuid = UUID.randomUUID();
        messageHooks.put(uuid, new HookInfo(what, arg1, requireTriggerPressed, hook));
        return uuid;
    }

    UUID addInternalHook(int what, int arg1, boolean requireTriggerPressed, BiConsumer<Integer, Object> hook) {
        UUID uuid = UUID.randomUUID();
        internalHooks.put(uuid, new HookInfo(what, arg1, requireTriggerPressed, hook));
        return uuid;
    }

    public void removeMessageHook(UUID hookId) {
        messageHooks.remove(hookId);
    }

    public void removeInternalHook(UUID hookId) {
        internalHooks.remove(hookId);
    }

    public void clearHooks() {
        messageHooks.clear();
    }

}
