package adris.altoclef;

import adris.altoclef.commands.CommandException;
import baritone.api.event.events.ChatEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

/**
 * Mixins have no way (currently) to access our mod.
 *
 * As a result I'll do this statically.
 *
 * However, I want to avoid grabbing AltoClef in a static context, so this class
 * serves at the "static dumpster" which is the only spot where
 * I allow the bad practice of singletons to flourish
 */
public class StaticMixinHookups {

    private static AltoClef _mod;

    public static void hookupMod(AltoClef mod) {
        _mod = mod;
    }

    public static void onInitializeLoad() {
        _mod.onInitializeLoad();
    }

    public static void onClientTick() {
        _mod.onClientTick();
    }

    public static void onClientRenderOverlay(MatrixStack stack) {_mod.onClientRenderOverlay(stack);}


    // Every chat message can be interrupted by us
    public static void onChat(ChatEvent e) {
        String line = e.getMessage();
        if (_mod.getCommandExecutor().isClientCommand(line)) {
            e.cancel();
            try {
                _mod.getCommandExecutor().Execute(line);
            } catch (CommandException ex) {
                Debug.logWarning(ex.getMessage());
                //ex.printStackTrace();
            }
        }
    }

    public static void onBlockBreaking(BlockPos pos, double progress) {
        _mod.getControllerExtras().onBlockBreak(pos, progress);
    }

    public static void onScreenOpen(Screen screen) {
        if (screen instanceof FurnaceScreen) {
            _mod.getContainerTracker().onFurnaceScreenOpen(((FurnaceScreen) screen).getScreenHandler());
        } else if (screen instanceof GenericContainerScreen) {
            _mod.getContainerTracker().onChestScreenOpen(((GenericContainerScreen) screen).getScreenHandler());
        } else if (screen == null) {
            _mod.getContainerTracker().onScreenClose();
        }

    }

    public static void onBlockInteract(BlockHitResult hitResult, BlockState blockState) {
        _mod.getContainerTracker().onBlockInteract(hitResult.getBlockPos(), blockState.getBlock());
    }
}
