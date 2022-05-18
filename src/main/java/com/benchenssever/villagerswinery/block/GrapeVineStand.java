package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

import java.util.Random;

public class GrapeVineStand extends VineStand implements IGrowable, ICrop, ISpreadCount {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    public static final IntegerProperty SPREAD = ISpreadCount.SPREAD;

    public GrapeVineStand(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AGE, 0).with(SPREAD, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE;}

    @Override
    public IntegerProperty getSpreadProperty() { return SPREAD; }

    @Override
    public Item getProduct() { return RegistryEvents.grape.get(); }

    @Override
    public Item getVine() { return RegistryEvents.grapeVineItem.get(); }

    @Override
    public float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) { return 1.0F; }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int spread = this.chickSpread(worldIn, pos);
        if(spread > this.getMaxSpread()) { spread = this.getMaxSpread();}
        this.withSpread(state, spread);
        if(this.getSpread(state) < this.getMaxSpread() - 1) { super.randomTick(state, worldIn, pos, random);}
        if(this.getSpread(state) > 1 && this.getSpread(state) < this.getMaxSpread()) { this.growth(state, worldIn, pos, random);}
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if(ItemTags.getCollection().get(new ResourceLocation("forge", "shears")).contains(stack.getItem())) {
            if(!worldIn.isRemote()) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if(this.isMaxAge(state)) {
                    worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getProduct().getDefaultInstance()));
                    worldIn.setBlockState(pos, state.with(this.getAgeProperty(), 0), 2);
                } else {
                    worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getVine().getDefaultInstance()));
                    worldIn.setBlockState(pos, this.getStand().getDefaultState(), 2);
                }

                if (!player.abilities.isCreativeMode) { stack.attemptDamageItem(1, new Random(), (ServerPlayerEntity) player); }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE, SPREAD);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) { return !this.isMaxAge(state); }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) { return true; }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if(age > this.getMaxAge()) age = this.getMaxAge();
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }
}
