package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CropVine extends VineBlock implements IGrowable, ICrop {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    private final RegistryObject<Item> product;

    public CropVine(Properties properties, RegistryObject<Item> product) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
        this.product = product;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public Item getProduct() {
        return this.product.get();
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, World world, BlockPos pos) {
        return this.isMaxAge(world.getBlockState(pos));
    }

    @Override
    public @NotNull List<ItemStack> onSheared(PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        if (this.isMaxAge(world.getBlockState(pos))) drops.add(new ItemStack(this.getProduct()));
        return drops;
    }

    @Override
    public float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerWorld worldIn, @NotNull BlockPos pos, @NotNull Random random) {
        super.randomTick(state, worldIn, pos, random);
        this.growth(state, worldIn, pos, random);
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
                worldIn.setBlockState(pos, this.withAge(state, 0), 2);
                if (!player.abilities.isCreativeMode) {
                    stack.attemptDamageItem(1, new Random(), (ServerPlayerEntity) player);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void fillStateContainer(StateContainer.@NotNull Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(@NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        return !this.isMaxAge(state);
    }

    @Override
    public boolean canUseBonemeal(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, @NotNull Random rand, @NotNull BlockPos pos, @NotNull BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if (age > this.getMaxAge()) {
            age = this.getMaxAge();
        }
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }
}