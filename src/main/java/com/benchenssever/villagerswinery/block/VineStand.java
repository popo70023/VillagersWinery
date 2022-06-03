package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;

import java.util.Random;

public class VineStand extends Stand implements IOnStand, IForgeShearable {

    public VineStand(Properties properties) {
        super(properties);
        listOnStandBlock.add(this);
    }

    public Item getVine() { return Items.VINE; }

    public Block getStand() { return RegistryEvents.stand.get(); }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if(ItemTags.getCollection().get(new ResourceLocation("forge", "shears")).contains(stack.getItem())) {
            if(!worldIn.isRemote()) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getVine().getDefaultInstance()));
                worldIn.setBlockState(pos, this.getStand().getDefaultState(), 2);
                if (!player.abilities.isCreativeMode) { stack.attemptDamageItem(1, new Random(), (ServerPlayerEntity) player); }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (worldIn.rand.nextInt(4) == 0 && worldIn.isAreaLoaded(pos, 4)) {
            BlockPos growPos = pos.offset(Direction.getRandomDirection(random));
            if(worldIn.getBlockState(growPos).getBlock().matchesBlock(this.getStand())) {
                worldIn.setBlockState(growPos, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this.getVine());
    }

    @Override
    public boolean putOnStand(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        if(stack.getItem() == this.getVine() && ICrop.isDirtGround(worldIn.getBlockState(pos.down()))) {
            if(!worldIn.isRemote()) {
                worldIn.playSound(null, pos, this.getSoundType(this.getDefaultState(), worldIn, pos, player).getPlaceSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, this.getDefaultState(), 2);
                if (!player.abilities.isCreativeMode){stack.shrink(1);}
            }

            return true;
        }

        return false;
    }
}
