package gg.rohan.narwhal.rfid;

import android.content.Context;
import android.util.Pair;

import java.util.UUID;
import java.util.function.Consumer;

import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import gg.rohan.narwhal.rfid.message.InventoryMessage;
import gg.rohan.narwhal.rfid.message.LocateMessage;
import gg.rohan.narwhal.rfid.result.ReaderConnectResult;
import gg.rohan.narwhal.rfid.result.ReaderConnectState;
import gg.rohan.narwhal.rfid.result.ReaderDisconnectResult;
import gg.rohan.narwhal.rfid.result.ReaderOpenResult;
import gg.rohan.narwhal.rfid.result.ReaderStartInventoryResult;
import gg.rohan.narwhal.rfid.result.ReaderStartLocatingResult;
import gg.rohan.narwhal.rfid.result.ReaderStopInventoryResult;
import gg.rohan.narwhal.rfid.result.ReaderWakeupResult;

public class ReaderStaticWrapper {

    private static Reader reader;
    private static ReaderHandler readerHandler;
    private static boolean triggerPressed = false;

    public static void initializeHandler() {
        if (readerHandler == null) {
            readerHandler = new ReaderHandler();
            readerHandler.addInternalHook(SDConsts.Msg.SDMsg, SDConsts.SDCmdMsg.TRIGGER_PRESSED, false, (arg2, obj) -> triggerPressed = true);
            readerHandler.addInternalHook(SDConsts.Msg.SDMsg, SDConsts.SDCmdMsg.TRIGGER_RELEASED, false, (arg2, obj) -> triggerPressed = false);
        } else throw new IllegalStateException("Handler already initialized");
    }

    public static void reInstallReader(final Context context) {
        reader = Reader.getReader(context, readerHandler);
    }

    public static void clearCurrentTasks() {
        stopInventory();
        readerHandler.clearHooks();
    }

    public static ReaderOpenResult openReaderConnection() {
        return ReaderOpenResult.fromBoolean(reader.SD_Open());
    }

    public static void closeReaderConnection() {
        reader.SD_Close();
    }

    public static ReaderWakeupResult wakeupReader() {
        return ReaderWakeupResult.fromInt(reader.SD_Wakeup());
    }

    public static ReaderConnectResult connectReader() {
        return ReaderConnectResult.fromInt(reader.SD_Connect());
    }

    public static ReaderDisconnectResult disconnectReader() {
        return ReaderDisconnectResult.fromInt(reader.SD_Disconnect());
    }

    public static ReaderStartInventoryResult startInventory() {
        return ReaderStartInventoryResult.fromInt(reader.RF_PerformInventory(false, false, true));
    }

    public static ReaderStopInventoryResult stopInventory() {
        return ReaderStopInventoryResult.fromInt(reader.RF_StopInventory());
    }

    public static ReaderStartLocatingResult startLocating(String tagId) {
        return ReaderStartLocatingResult.fromInt(reader.RF_PerformInventoryForLocating(tagId));
    }

    public static ReaderConnectState getConnectionState() {
        return ReaderConnectState.fromInt(reader.SD_GetConnectState());
    }

    public static UUID addInventoryHook(Consumer<InventoryMessage> receiver) {
        return readerHandler.addMessageHook(SDConsts.Msg.RFMsg, SDConsts.RFCmdMsg.INVENTORY, true, (arg2, obj) -> receiver.accept(InventoryMessage.fromString((String) obj)));
    }

    public static UUID addLocatingHook(Consumer<LocateMessage> receiver) {
        return readerHandler.addMessageHook(SDConsts.Msg.RFMsg, SDConsts.RFCmdMsg.LOCATE, true, (arg2, obj) -> receiver.accept(LocateMessage.fromString(obj.toString())));
    }

    public static void removeMessageHook(UUID hookId) {
        readerHandler.removeMessageHook(hookId);
    }

    public static Pair<UUID, UUID> addTriggerHook(Consumer<Boolean> receiver) {
        UUID pressedListener = readerHandler.addMessageHook(SDConsts.Msg.SDMsg, SDConsts.SDCmdMsg.TRIGGER_PRESSED, false, (arg2, obj) -> receiver.accept(true));
        UUID releasedListener = readerHandler.addMessageHook(SDConsts.Msg.SDMsg, SDConsts.SDCmdMsg.TRIGGER_RELEASED, false, (arg2, obj) -> receiver.accept(false));
        return new Pair<>(pressedListener, releasedListener);
    }

    public static Pair<UUID, UUID> autoPerformInventory() {
        return addTriggerHook(state -> {
            if (state) {
                startInventory();
            } else {
                stopInventory();
            }
        });
    }

    public static Pair<UUID, UUID> autoPerformLocating(String tagId) {
        System.out.println("Auto locating: " + tagId);
        return addTriggerHook(state -> {
            if (state) {
                startLocating(tagId);
            } else {
                stopInventory();
            }
        });
    }

    public static boolean isTriggerPressed() {
        return triggerPressed;
    }

}
