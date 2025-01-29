package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VineStand extends Stand implements IOnStand, IForgeShearable {
    private final RegistryObject<Block> modvine;
    private final Item vine;

    public VineStand(Properties properties, Item vine) {
        super(properties);
        this.vine = vine;
        this.modvine = null;
        listOnStandBlock.add(this);
    }

    public VineStand(Properties properties, RegistryObject<Block> vine) {
        super(properties);
        this.vine = null;
        this.modvine = vine;
        listOnStandBlock.add(this);
    }

    public Item getVineItem() {
        return this.vine != null ? this.vine : this.modvine.get().asItem();
    }

    public Block getStand() {
        return RegistryEvents.stand.get();
    }

    @Override
    public @NotNull List<ItemStack> onSheared(PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this.getVineItem()));
        return drops;
    }

    @Override
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if (ItemTags.getCollection().get(new ResourceLocation("forge", "shears")).contains(stack.getItem()) && this.isShearable(stack, worldIn, pos)) {
            worldIn.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!worldIn.isRemote()) {
                List<ItemStack> drops = onSheared(player, stack, worldIn, pos, 0);
                for (ItemStack drop : drops) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop);
                }
                worldIn.setBlockState(pos, this.getStand().getDefaultState(), 2);
                if (!player.abilities.isCreativeMode) {
                    stack.damageItem(1, player, (p) -> p.sendBreakAnimation(handIn));
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerWorld worldIn, @NotNull BlockPos pos, @NotNull Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (worldIn.rand.nextInt(4) == 0 && worldIn.isAreaLoaded(pos, 4)) {
            BlockPos growPos = pos.offset(Direction.getRandomDirection(random));
            if (worldIn.getBlockState(growPos).getBlock().matchesBlock(this.getStand())) {
                worldIn.setBlockState(growPos, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this.getVineItem());
    }

    @Override
    public Item itemOnStand() {
        return this.getVineItem();
    }

    @Override
    public boolean putOnStand(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        if (ICrop.isDirtGround(worldIn.getBlockState(pos.down()))) {
            worldIn.playSound(player, pos, this.getSoundType(this.getDefaultState(), worldIn, pos, player).getPlaceSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!worldIn.isRemote()) {
                worldIn.setBlockState(pos, this.getDefaultState(), 2);
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }

            return true;
        }

        return false;
    }
}
