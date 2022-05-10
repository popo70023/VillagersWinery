package com.benchenssever.villagerswinery.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class VineSeeds extends Item {
    private final Block vineBlock;

    public VineSeeds(Block vineBlock, Properties properties) {
        super(properties);
        this.vineBlock = vineBlock;
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(context));
        return !actionresulttype.isSuccessOrConsume() && this.isFood() ? this.onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType() : actionresulttype;
    }

    public ActionResultType tryPlace(BlockItemUseContext context) {
        return ActionResultType.FAIL;
    }
}
