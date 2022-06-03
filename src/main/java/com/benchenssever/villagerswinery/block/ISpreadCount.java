package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISpreadCount {
    IntegerProperty SPREAD = IntegerProperty.create("spread", 0, 11);

    IntegerProperty getSpreadProperty();

    default int getSpread(BlockState state){ return state.get(this.getSpreadProperty());}

    default int getMaxSpread() { return this.getSpreadProperty().getAllowedValues().size() - 1;};

    default BlockState withSpread(BlockState state, int spread) {return state.with(this.getSpreadProperty(), spread);}

    default boolean isMaxSpread(BlockState state){return this.getSpread(state) >= this.getMaxSpread();}

    default int chickSpread(World worldIn, BlockPos pos) {
        if(!ICrop.isDirtGround(worldIn.getBlockState(pos.down()))) {
            int spread = Integer.MAX_VALUE;
            for(Direction direction: Direction.values()) {
                BlockState checkBlockState = worldIn.getBlockState(pos.offset(direction));
                if(checkBlockState.matchesBlock((Block) this) && this.getSpread(checkBlockState) < spread) {
                    spread = this.getSpread(checkBlockState);
                }
            }
            if(spread != Integer.MAX_VALUE) { spread++;}
            return spread;
        }
        return 0;
    }
}
