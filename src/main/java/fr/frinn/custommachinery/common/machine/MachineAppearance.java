package fr.frinn.custommachinery.common.machine;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import fr.frinn.custommachinery.api.ICustomMachineryAPI;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.machine.IMachineAppearance;
import fr.frinn.custommachinery.api.machine.MachineAppearanceProperty;
import fr.frinn.custommachinery.common.init.Registration;
import fr.frinn.custommachinery.common.util.MachineShape;
import fr.frinn.custommachinery.impl.codec.NamedMapCodec;
import fr.frinn.custommachinery.impl.util.IMachineModelLocation;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class MachineAppearance implements IMachineAppearance {

    public static final NamedMapCodec<Map<MachineAppearanceProperty<?>, Object>> CODEC = new NamedMapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Registration.APPEARANCE_PROPERTY_REGISTRY.keys(ops);
        }

        @Override
        public <T> DataResult<Map<MachineAppearanceProperty<?>, Object>> decode(DynamicOps<T> ops, MapLike<T> input) {
            ImmutableMap.Builder<MachineAppearanceProperty<?>, Object> properties = ImmutableMap.builder();

            for(MachineAppearanceProperty<?> property : Registration.APPEARANCE_PROPERTY_REGISTRY) {
                if(property.getId() != null && input.get(property.getId().toString()) != null) {
                    DataResult<?> result = property.getCodec().read(ops, input.get(property.getId().toString()));
                    if(result.result().isPresent())
                        properties.put(property, result.result().get());
                    else if(result.error().isPresent()) {
                        ICustomMachineryAPI.INSTANCE.logger().warn("Couldn't deserialize appearance property: {}, invalid value: {}, error: {}, using default value instead.", property.getId(), input.get(property.getId().toString()), result.error().get().message());
                        properties.put(property, property.getDefaultValue());
                    }
                } else if(property.getId() != null && input.get(property.getId().getPath()) != null) {
                    DataResult<?> result = property.getCodec().read(ops, input.get(property.getId().getPath()));
                    if(result.result().isPresent())
                        properties.put(property, result.result().get());
                    else if(result.error().isPresent()) {
                        ICustomMachineryAPI.INSTANCE.logger().warn("Couldn't deserialize appearance property: {}, invalid value: {}, error: {}, using default value instead.", property.getId(), input.get(property.getId().getPath()), result.error().get().message());
                        properties.put(property, property.getDefaultValue());
                    }
                } else {
                    properties.put(property, property.getDefaultValue());
                }
            }

            return DataResult.success(properties.build());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> RecordBuilder<T> encode(Map<MachineAppearanceProperty<?>, Object> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            for(Map.Entry<MachineAppearanceProperty<?>, Object> entry : input.entrySet()) {
                if(!Objects.equals(entry.getValue() ,entry.getKey().getDefaultValue()) && entry.getKey().getId() != null)
                    prefix.add(entry.getKey().getId().toString(), ((NamedCodec<Object>)entry.getKey().getCodec()).encodeStart(ops, entry.getValue()));
            }
            return prefix;
        }

        @Override
        public String name() {
            return "Machine Appearance";
        }
    };

    public static final MachineAppearance DEFAULT = new MachineAppearance(defaultProperties());

    private final Map<MachineAppearanceProperty<?>, Object> properties;

    public MachineAppearance(Map<MachineAppearanceProperty<?>, Object> properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(MachineAppearanceProperty<T> property) {
        if(!this.properties.containsKey(property))
            return property.getDefaultValue();
        return (T)this.properties.get(property);
    }

    @Override
    public IMachineModelLocation getBlockModel() {
        return getProperty(Registration.BLOCK_MODEL_PROPERTY.get());
    }

    @Override
    public IMachineModelLocation getItemModel() {
        return getProperty(Registration.ITEM_MODEL_PROPERTY.get());
    }

    @Override
    public SoundEvent getAmbientSound() {
        return getProperty(Registration.AMBIENT_SOUND_PROPERTY.get());
    }

    @Override
    public SoundType getInteractionSound() {
        return getProperty(Registration.INTERACTION_SOUND_PROPERTY.get());
    }

    @Override
    public int getLightLevel() {
        return getProperty(Registration.LIGHT_PROPERTY.get());
    }

    @Override
    public int getColor() {
        return getProperty(Registration.COLOR_PROPERTY.get());
    }

    @Override
    public float getHardness() {
        return getProperty(Registration.HARDNESS_PROPERTY.get());
    }

    @Override
    public float getResistance() {
        return getProperty(Registration.RESISTANCE_PROPERTY.get());
    }

    @Override
    public List<TagKey<Block>> getTool() {
        return getProperty(Registration.TOOL_TYPE_PROPERTY.get());
    }

    @Override
    public TagKey<Block> getMiningLevel() {
        return getProperty(Registration.MINING_LEVEL_PROPERTY.get());
    }

    @Override
    public boolean requiresCorrectToolForDrops() {
        return getProperty(Registration.REQUIRES_TOOL.get());
    }

    @Override
    public MachineShape getShape() {
        return getProperty(Registration.SHAPE_PROPERTY.get());
    }

    @Override
    public Function<Direction, VoxelShape> getCollisionShape() {
        MachineShape collisionShape = getProperty(Registration.SHAPE_COLLISION_PROPERTY.get());
        if(collisionShape == MachineShape.DEFAULT_COLLISION)
            return getShape();
        return collisionShape;
    }

    @Override
    public MachineAppearance copy() {
        return new MachineAppearance(ImmutableMap.copyOf(this.properties));
    }

    public Map<MachineAppearanceProperty<?>, Object> getProperties() {
        return this.properties;
    }

    public static Map<MachineAppearanceProperty<?>, Object> defaultProperties() {
        Map<MachineAppearanceProperty<?>, Object> map = new HashMap<>();

        for(MachineAppearanceProperty<?> property : Registration.APPEARANCE_PROPERTY_REGISTRY)
            map.put(property, property.getDefaultValue());

        return map;
    }
}
