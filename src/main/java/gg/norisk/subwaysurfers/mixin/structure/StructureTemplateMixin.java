package gg.norisk.subwaysurfers.mixin.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import gg.norisk.subwaysurfers.client.structure.ClientStructureTemplate;
import gg.norisk.subwaysurfers.entity.UUIDMarker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements ClientStructureTemplate {

    @Shadow
    @Final
    private List<StructureTemplate.PalettedBlockInfoList> blockInfoLists;

    @Shadow
    @Final
    private List<StructureTemplate.StructureEntityInfo> entities;

    @Shadow
    private Vec3i size;

    @Shadow
    public static void updateCorner(WorldAccess worldAccess, int i, VoxelSet voxelSet, int j, int k, int l) {
    }

    private void spawnEntities(ClientWorld world, BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2, @Nullable BlockBox blockBox, boolean bl) {
        Iterator<StructureTemplate.StructureEntityInfo> var8 = this.entities.iterator();

        while (true) {
            StructureTemplate.StructureEntityInfo structureEntityInfo;
            BlockPos blockPos3;
            do {
                if (!var8.hasNext()) {
                    return;
                }

                structureEntityInfo = (StructureTemplate.StructureEntityInfo) var8.next();
                blockPos3 = transformAround(structureEntityInfo.blockPos, blockMirror, blockRotation, blockPos2).add(blockPos);
            } while (blockBox != null && !blockBox.contains(blockPos3));

            NbtCompound nbtCompound = structureEntityInfo.nbt.copy();
            Vec3d vec3d = transformAround(structureEntityInfo.pos, blockMirror, blockRotation, blockPos2);
            Vec3d vec3d2 = vec3d.add((double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ());
            NbtList nbtList = new NbtList();
            nbtList.add(NbtDouble.of(vec3d2.x));
            nbtList.add(NbtDouble.of(vec3d2.y));
            nbtList.add(NbtDouble.of(vec3d2.z));
            nbtCompound.put("Pos", nbtList);
            nbtCompound.remove("UUID");
            EntityType.getEntityFromNbt(nbtCompound, world).ifPresent(entity -> {
                float f = entity.applyRotation(blockRotation);
                f += entity.applyMirror(blockMirror) - entity.getYaw();
                entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.getPitch());
                if (entity instanceof UUIDMarker uuidMarker) {
                    //TODO this is fine for now ig
                    if (MinecraftClient.getInstance().player != null) {
                        uuidMarker.setOwner(MinecraftClient.getInstance().player.getUuid());
                    }
                }

                world.addEntity(world.random.nextInt(), entity);
                entity.streamSelfAndPassengers().forEach(world::spawnEntity);
            });
        }
    }

    @Shadow
    public static BlockPos transform(StructurePlacementData structurePlacementData, BlockPos blockPos) {
        return null;
    }

    @Shadow
    public static BlockPos transformAround(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2) {
        return null;
    }

    @Shadow
    public static Vec3d transformAround(Vec3d vec3d, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos) {
        return null;
    }

    @NotNull
    @Shadow
    private static Optional<Entity> getEntity(ServerWorldAccess serverWorldAccess, NbtCompound nbtCompound) {
        return Optional.empty();
    }

    @Unique
    private static List processClient(ClientWorld clientWorld, BlockPos blockPos, BlockPos blockPos2, StructurePlacementData structurePlacementData, List<StructureTemplate.StructureBlockInfo> list) {
        List<StructureTemplate.StructureBlockInfo> list2 = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> list3 = new ArrayList<>();
        Iterator var7 = list.iterator();

        while (var7.hasNext()) {
            StructureTemplate.StructureBlockInfo structureBlockInfo = (StructureTemplate.StructureBlockInfo) var7.next();
            BlockPos blockPos3 = transform(structurePlacementData, structureBlockInfo.comp_1341()).add(blockPos);
            StructureTemplate.StructureBlockInfo structureBlockInfo2 = new StructureTemplate.StructureBlockInfo(blockPos3, structureBlockInfo.comp_1342(), structureBlockInfo.comp_1343() != null ? structureBlockInfo.comp_1343().copy() : null);

            for (Iterator<StructureProcessor> iterator = structurePlacementData.getProcessors().iterator(); structureBlockInfo2 != null && iterator.hasNext(); structureBlockInfo2 = ((StructureProcessor) iterator.next()).process(clientWorld, blockPos, blockPos2, structureBlockInfo, structureBlockInfo2, structurePlacementData)) {
            }

            if (structureBlockInfo2 != null) {
                list3.add(structureBlockInfo2);
                list2.add(structureBlockInfo);
            }
        }

        return (List) list3;
    }

    @Unique
    public boolean placeClient(ClientWorld clientWorld, BlockPos blockPos, BlockPos blockPos2, StructurePlacementData structurePlacementData, Random random, int i) {
        if (this.blockInfoLists.isEmpty()) {
            return false;
        } else {
            List<StructureTemplate.StructureBlockInfo> list = structurePlacementData.getRandomBlockInfos(this.blockInfoLists, blockPos).getAll();
            if ((!list.isEmpty() || !structurePlacementData.shouldIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
                BlockBox blockBox = structurePlacementData.getBoundingBox();
                List<BlockPos> list2 = Lists.newArrayListWithCapacity(structurePlacementData.shouldPlaceFluids() ? list.size() : 0);
                List<BlockPos> list3 = Lists.newArrayListWithCapacity(structurePlacementData.shouldPlaceFluids() ? list.size() : 0);
                List<Pair<BlockPos, NbtCompound>> list4 = Lists.newArrayListWithCapacity(list.size());
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MAX_VALUE;
                int m = Integer.MIN_VALUE;
                int n = Integer.MIN_VALUE;
                int o = Integer.MIN_VALUE;
                List<StructureTemplate.StructureBlockInfo> list5 = processClient(clientWorld, blockPos, blockPos2, structurePlacementData, list);
                Iterator<StructureTemplate.StructureBlockInfo> var19 = list5.iterator();

                while (true) {
                    StructureTemplate.StructureBlockInfo structureBlockInfo;
                    BlockPos blockPos3;
                    BlockEntity blockEntity;
                    do {
                        if (!var19.hasNext()) {
                            boolean bl = true;
                            Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                            Iterator iterator;
                            int p;
                            BlockState blockState2;
                            while (bl && !list2.isEmpty()) {
                                bl = false;
                                iterator = list2.iterator();

                                while (iterator.hasNext()) {
                                    BlockPos blockPos4 = (BlockPos) iterator.next();
                                    FluidState fluidState2 = clientWorld.getFluidState(blockPos4);

                                    for (p = 0; p < directions.length && !fluidState2.isStill(); ++p) {
                                        BlockPos blockPos5 = blockPos4.offset(directions[p]);
                                        FluidState fluidState3 = clientWorld.getFluidState(blockPos5);
                                        if (fluidState3.isStill() && !list3.contains(blockPos5)) {
                                            fluidState2 = fluidState3;
                                        }
                                    }

                                    if (fluidState2.isStill()) {
                                        blockState2 = clientWorld.getBlockState(blockPos4);
                                        Block block = blockState2.getBlock();
                                        if (block instanceof FluidFillable) {
                                            ((FluidFillable) block).tryFillWithFluid(clientWorld, blockPos4, blockState2, fluidState2);
                                            bl = true;
                                            iterator.remove();
                                        }
                                    }
                                }
                            }

                            if (j <= m) {
                                if (!structurePlacementData.shouldUpdateNeighbors()) {
                                    VoxelSet voxelSet = new BitSetVoxelSet(m - j + 1, n - k + 1, o - l + 1);
                                    int q = j;
                                    int r = k;
                                    p = l;

                                    for (Pair<BlockPos, NbtCompound> blockPosNbtCompoundPair : list4) {
                                        BlockPos blockPos6 = blockPosNbtCompoundPair.getFirst();
                                        voxelSet.set(blockPos6.getX() - q, blockPos6.getY() - r, blockPos6.getZ() - p);
                                    }

                                    updateCorner(clientWorld, i, voxelSet, q, r, p);
                                }

                                iterator = list4.iterator();

                                while (iterator.hasNext()) {
                                    Pair<BlockPos, NbtCompound> pair2 = (Pair) iterator.next();
                                    BlockPos blockPos7 = (BlockPos) pair2.getFirst();
                                    if (!structurePlacementData.shouldUpdateNeighbors()) {
                                        blockState2 = clientWorld.getBlockState(blockPos7);
                                        BlockState blockState3 = Block.postProcessState(blockState2, clientWorld, blockPos7);
                                        if (blockState2 != blockState3) {
                                            clientWorld.setBlockState(blockPos7, blockState3, i & -2 | 16);
                                        }

                                        clientWorld.updateNeighbors(blockPos7, blockState3.getBlock());
                                    }

                                    if (pair2.getSecond() != null) {
                                        blockEntity = clientWorld.getBlockEntity(blockPos7);
                                        if (blockEntity != null) {
                                            blockEntity.markDirty();
                                        }
                                    }
                                }
                            }

                            if (!structurePlacementData.shouldIgnoreEntities()) {
                                this.spawnEntities(clientWorld, blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition(), blockBox, structurePlacementData.shouldInitializeMobs());
                            }

                            return true;
                        }

                        structureBlockInfo = var19.next();
                        blockPos3 = structureBlockInfo.comp_1341();
                    } while (blockBox != null && !blockBox.contains(blockPos3));

                    FluidState fluidState = structurePlacementData.shouldPlaceFluids() ? clientWorld.getFluidState(blockPos3) : null;
                    BlockState blockState = structureBlockInfo.comp_1342().mirror(structurePlacementData.getMirror()).rotate(structurePlacementData.getRotation());
                    if (structureBlockInfo.comp_1343() != null) {
                        blockEntity = clientWorld.getBlockEntity(blockPos3);
                        Clearable.clear(blockEntity);
                        clientWorld.setBlockState(blockPos3, Blocks.BARRIER.getDefaultState(), 20);
                    }

                    if (clientWorld.setBlockState(blockPos3, blockState, i)) {
                        j = Math.min(j, blockPos3.getX());
                        k = Math.min(k, blockPos3.getY());
                        l = Math.min(l, blockPos3.getZ());
                        m = Math.max(m, blockPos3.getX());
                        n = Math.max(n, blockPos3.getY());
                        o = Math.max(o, blockPos3.getZ());
                        list4.add(Pair.of(blockPos3, structureBlockInfo.comp_1343()));
                        if (structureBlockInfo.comp_1343() != null) {
                            blockEntity = clientWorld.getBlockEntity(blockPos3);
                            if (blockEntity != null) {
                                if (blockEntity instanceof LootableContainerBlockEntity) {
                                    structureBlockInfo.comp_1343().putLong("LootTableSeed", random.nextLong());
                                }

                                blockEntity.readNbt(structureBlockInfo.comp_1343());
                            }
                        }

                        if (fluidState != null) {
                            if (blockState.getFluidState().isStill()) {
                                list3.add(blockPos3);
                            } else if (blockState.getBlock() instanceof FluidFillable) {
                                ((FluidFillable) blockState.getBlock()).tryFillWithFluid(clientWorld, blockPos3, blockState, fluidState);
                                if (!fluidState.isStill()) {
                                    list2.add(blockPos3);
                                }
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        }
    }

}
