package fr.frinn.custommachinery.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.common.machine.CustomMachine;
import fr.frinn.custommachinery.common.machine.MachineLocation;
import fr.frinn.custommachinery.common.network.SUpdateMachinesPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void writeNewMachineJson(MinecraftServer server, CustomMachine machine, boolean kubejs) {
        if(kubejs && !Platform.isModLoaded("kubejs")) {
            CustomMachinery.LOGGER.error("Can't write new machine json {} in kubejs data folder because KubeJS isn't present", machine.getId());
            return;
        }
        DataResult<JsonElement> result = CustomMachine.CODEC.encodeStart(JsonOps.INSTANCE, machine);
        if(result.error().isPresent()) {
            CustomMachinery.LOGGER.error("Can't write new machine json: {}\n{}", machine.getId().getPath(), result.error().get().message());
            return;
        }
        if(result.result().isPresent()) {
            JsonElement json = result.result().get();
            String root = server.getServerDirectory().getAbsolutePath();

            if (root.endsWith(File.separator))
                root = root.substring(0, root.length() - 2);
            if(kubejs)
                root = root + File.separator + "kubejs" + File.separator + "data" + File.separator + machine.getId().getNamespace() + File.separator + "machines";
            File file = new File(root, machine.getId().getPath() + ".json");
            CustomMachinery.LOGGER.info("Writing new machine: {} in {}", machine.getLocation().getId(), file.getPath());
            try {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        CustomMachinery.LOGGER.error("Can't create directory for '{}'", file.getParentFile().getAbsolutePath());
                    }
                }
                if(file.exists() || file.createNewFile()) {
                    JsonWriter writer = GSON.newJsonWriter(new FileWriter(file));
                    GSON.toJson(json, writer);
                    writer.close();
                    if(kubejs) {
                        //Immediately update machine list
                        CustomMachinery.MACHINES.put(machine.getId(), machine);
                        MachineList.setNeedRefresh();
                        new SUpdateMachinesPacket(CustomMachinery.MACHINES).sendToAll(server);
                    }
                } else {
                    CustomMachinery.LOGGER.error("Can't write new machine file in '{}'", file.getAbsolutePath());
                }
            } catch (IOException e) {
                CustomMachinery.LOGGER.error("Error while writing new machine to file: {}\n{}\n{}", file.getAbsolutePath(), e.getMessage(), ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static void writeMachineJson(MinecraftServer server, CustomMachine machine) {
        MachineLocation location = machine.getLocation();
        File machineJson = location.getFile(server);
        if(machineJson == null) {
            CustomMachinery.LOGGER.error("Error while editing machine: {}\nCan't edit machine loaded with {}", location.getId(), location.getLoader().toString());
            return;
        } else if(!machineJson.exists() || machineJson.isDirectory()) {
            CustomMachinery.LOGGER.error("Error while editing machine: {}\nFile '{}' doesn't exist", location.getId(), machineJson.getAbsolutePath());
            return;
        }
        try(JsonWriter writer = GSON.newJsonWriter(new FileWriter(machineJson))) {
            DataResult<JsonElement> result = CustomMachine.CODEC.encodeStart(MachineJsonOps.INSTANCE, machine);
            if(result.error().isPresent()) {
                CustomMachinery.LOGGER.error("Can't edit machine json: {}\n{}", machine.getId().getPath(), result.error().get().message());
                return;
            }
            if(result.result().isPresent()) {
                JsonElement json = result.result().get();
                GSON.toJson(json, writer);
                CustomMachinery.LOGGER.info("Successfully edited machine: {} at location '{}'", location.getId(), machineJson.getAbsolutePath());
            }
        } catch (IOException e) {
            CustomMachinery.LOGGER.error("Error while editing machine to file: {}\n{}\n{}", machineJson.getAbsolutePath(), e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    public static void deleteMachineJson(MinecraftServer server, MachineLocation location) {
        File machineJson = location.getFile(server);
        if(machineJson == null) {
            CustomMachinery.LOGGER.error("Error while deleting machine: {}\nCan't delete machine loaded with {}", location.getId(), location.getLoader().toString());
            return;
        } else if(!machineJson.exists() || machineJson.isDirectory()) {
            CustomMachinery.LOGGER.error("Error while deleting machine: {}\nFile '{}' doesn't exist", location.getId(), machineJson.getAbsolutePath());
            return;
        } else if(!machineJson.delete()) {
            CustomMachinery.LOGGER.error("Error while deleting machine: {}\nFile '{}' can't be deleted", location.getId(), machineJson.getAbsolutePath());
            return;
        }
        CustomMachinery.LOGGER.info("Successfully deleted machine: {} at location '{}'", location.getId(), machineJson.getAbsolutePath());
    }
}
