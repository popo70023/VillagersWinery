package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class GrapeVine extends VineBlock implements IGrowable, ICrop {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

    public GrapeVine(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public int getMaxAge() { return 7; }

    @Override
    public float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) { return 1.0F; }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        this.growth(state, worldIn, pos, random);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if(stack.getItem() == Items.SHEARS && this.isMaxAge(state)) {
            if(!worldIn.isRemote()) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, RegistryEvents.grape.get().getDefaultInstance()));
                worldIn.setBlockState(pos, state.with(this.getAgeProperty(), 0), 2);
                if (!player.abilities.isCreativeMode) {
                    stack.attemptDamageItem(1, new Random(), (ServerPlayerEntity) player);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) { return !this.isMaxAge(state); }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if (age > this.getMaxAge()) {age = this.getMaxAge();}
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockState ground = worldIn.getBlockState(pos.down());
        return super.isValidPosition(state, worldIn, pos) && ICrop.isDirtGround(ground);
    }
}