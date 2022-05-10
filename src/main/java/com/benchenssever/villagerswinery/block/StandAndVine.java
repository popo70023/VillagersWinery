package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class StandAndVine extends CropsBlock {
    private final int heightLimit;
    public static final BooleanProperty MATURE = BooleanProperty.create("mature");
    private static final VoxelShape STAND_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public StandAndVine(Properties properties, int heightLimit) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(this.getAgeProperty(), 0).with(this.getMatureProperty(), false));
        this.heightLimit = heightLimit;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return STAND_SHAPE;
    }

    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.matchesBlock(Blocks.FARMLAND) || state.getBlock() instanceof StandAndVine;
    }

    public BooleanProperty getMatureProperty() {
        return MATURE;
    }

    protected int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }
    protected Boolean getMature(BlockState state) {
        return state.get(this.getMatureProperty());
    }

    public BlockState withAge(BlockState state, int age) {
        return state.with(this.getAgeProperty(), age);
    }

    public BlockState withMature(BlockState state, Boolean mature) {
        return state.with(this.getMatureProperty(), mature);
    }

    public boolean ticksRandomly(BlockState state) { return !this.getMature(state);}

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightSubtracted(pos, 0) >= 9) {
            float f = getGrowthChance(this, worldIn, pos);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0))
            {
                int h = this.heightCanGrowth(worldIn, pos);
                if (h != -1)
                {
                    this.growth(worldIn, pos.up(h));
                }
            }
        }

    }

    public int heightCanGrowth(IBlockReader theWorld, BlockPos pos) {
        for (int i = 0; i < heightLimit; i++) {

            if(theWorld.getBlockState(pos).getBlock() == this) {
                int age = this.getAge(theWorld.getBlockState(pos));

                if(age == 5) {
                    if(i == heightLimit - 1) {
                        return i;
                    }
                    else if(theWorld.getBlockState(pos.up()).getBlock() instanceof StandAndVine) {
                        if(theWorld.getBlockState(pos.up()).getBlock() == RegistryEvents.standBlock.get()) {
                            return (i + 1);
                        }
                    }
                    else {
                        return -1;
                    }
                }
                else if(age >= 7) {
                    return -1;
                }
                else {
                    return i;
                }
            }
            else {
                return -1;
            }
            pos = pos.up();
        }
        return -1;
    }

    public void growth(World theWorld, BlockPos pos)
    {
        BlockState state = theWorld.getBlockState(pos);

        if(state.getBlock() == this)
        {
            int age = this.getAge(state);
            theWorld.setBlockState(pos, this.withAge(state,age + 1), 2);
        }
        else if(state.getBlock() == RegistryEvents.standBlock.get())
        {
            theWorld.setBlockState(pos, this.withAge(1), 2);
        }
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        if(!isClient)
        {
            return heightCanGrowth(worldIn, pos) != -1 && worldIn.getBlockState(pos.down()).matchesBlock(Blocks.FARMLAND);
        }
        return false;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {

        return state.getBlock() != RegistryEvents.standBlock.get() && worldIn.getBlockState(pos.down()).matchesBlock(Blocks.FARMLAND);
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int g = this.getBonemealAgeIncrease(worldIn);

        for (int i = 0; i < g; i++)
        {
            int h = this.heightCanGrowth(worldIn, pos);
            if (h != -1)
            {
                this.growth(worldIn, pos.up(h));
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(MATURE);
    }
}
