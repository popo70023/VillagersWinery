package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CropVineStand extends VineStand implements IGrowable, ICrop {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    private final RegistryObject<Item> product;

    public CropVineStand(Properties properties, RegistryObject<Block> vine, RegistryObject<Item> product) {
        super(properties, vine);
        this.product = product;
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE;}

    @Override
    public Item getProduct() { return this.product.get(); }

    @Override
    public float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) { return 1.0F; }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this.isMaxAge(world.getBlockState(pos))? this.getProduct(): this.getVineItem()));
        return drops;
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if(ItemTags.getCollection().get(new ResourceLocation("forge", "shears")).contains(stack.getItem())) {
            worldIn.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if(!worldIn.isRemote()) {
                List<ItemStack> drops = onSheared(player, stack, worldIn, pos, 0);
                for (ItemStack drop : drops) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop);
                }
                if(this.isMaxAge(state)) {
                    worldIn.setBlockState(pos, state.with(this.getAgeProperty(), 0), 2);
                } else {
                    worldIn.setBlockState(pos, this.getStand().getDefaultState(), 2);
                }

                if (!player.abilities.isCreativeMode) { stack.damageItem(1, player, (p) -> p.sendBreakAnimation(handIn)); }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random random) {
        super.randomTick(state, worldIn, pos, random);
        this.growth(state, worldIn, pos, random);
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) { return !this.isMaxAge(state); }

    @Override
    public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

    @Override
    public void grow(@Nonnull ServerWorld worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if(age > this.getMaxAge()) age = this.getMaxAge();
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }
}
