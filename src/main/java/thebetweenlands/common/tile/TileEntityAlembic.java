package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityAlembic extends TileEntity implements ITickableTileEntity {
	
    public TileEntityAlembic(TileEntityType<?> te) {
		super(te);
	}

	public static final int DISTILLING_TIME = 4800; //4 Minutes

    public static final int AMOUNT_PER_VIAL = Amounts.VIAL;

    public static final float ISOLATION_LOSS_MULTIPLIER = 0.15F;

    private boolean running = false;
    private int progress = 0;
    private ItemStack infusionBucket = ItemStack.EMPTY;
    private int producedAmount = 0;
    private int producableAmount = 0;
    private int producableStrength;
    private int producableDuration;
    private ElixirEffect producableElixir = null;
    private List<Aspect> producableItemAspects = new ArrayList<Aspect>();
    private ElixirRecipe recipe = null;
    private int bucketInfusionTime;

    private boolean loadInfusionData = false;

    public void addInfusion(ItemStack bucket) {
        this.infusionBucket = bucket.copy();
        this.loadFromInfusion();
        setChanged();
    }


    @Override
    public void tick() {
        if (this.loadInfusionData) {
            this.loadFromInfusion();
            this.loadInfusionData = false;
        }

        if (this.isFull() && !this.hasFinished()) {
            this.progress++;
            if (!this.level.isClientSide()) {
                if (!this.running || this.progress % 20 == 0) {
                    setChanged();
                }
                this.running = true;
                if (this.hasFinished()) {
                    this.producedAmount = this.producableAmount;
                }
            }
        } else {
            if (!this.level.isClientSide()) {
                if (this.running) {
                    setChanged();
                }
                this.running = false;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        if (!this.infusionBucket.isEmpty())
            nbt.put("infusionBucket", this.infusionBucket.save(new CompoundNBT()));
        nbt.putInt("progress", this.progress);
        nbt.putInt("producedAmount", this.producedAmount);
        nbt.putBoolean("running", this.running);
        ListNBT aspectList = new ListNBT();
        for (Aspect aspect : this.producableItemAspects) {
            CompoundNBT aspectCompound = new CompoundNBT();
            aspect.save(aspectCompound);
            aspectList.add(aspectCompound);
        }
        nbt.put("producableItemAspects", aspectList);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("infusionBucket"))
            this.infusionBucket = new ItemStack(nbt.getCompound("infusionBucket"));
        this.loadInfusionData = true;
        this.progress = nbt.getInt("progress");
        this.producedAmount = nbt.getInt("producedAmount");
        this.running = nbt.getBoolean("running");
        if (nbt.contains("producableItemAspects")) {
            this.producableItemAspects.clear();
            ListNBT aspectList = nbt.getList("producableItemAspects", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < aspectList.size(); i++) {
                CompoundNBT aspectCompound = aspectList.getCompound(i);
                Aspect aspect = Aspect.load(aspectCompound);
                this.producableItemAspects.add(aspect);
            }
        }
    }

    @Override
    public void setChanged() {
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(getBlockPos(), state, state, 3);
        level.markBlockRangeForRenderUpdate(getBlockPos(), getBlockPos());
        super.setChanged();
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putBoolean("running", this.running);
        CompoundNBT itemStackCompound = new CompoundNBT();
        if (!this.infusionBucket.isEmpty()) {
            this.infusionBucket.save(itemStackCompound);
        }
        nbt.put("infusionBucket", itemStackCompound);
        nbt.putInt("progress", this.progress);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        this.running = tag.getBoolean("running");
        CompoundNBT itemStackCompound = tag.getCompound("infusionBucket");
        ItemStack oldStack = this.infusionBucket;
        if (itemStackCompound.contains("id") && !itemStackCompound.getString("id").isEmpty())
            this.infusionBucket = new ItemStack(itemStackCompound);
        else
            this.infusionBucket = ItemStack.EMPTY;
        if (!this.infusionBucket.isEmpty() && !ItemStack.areItemStacksEqual(this.infusionBucket, oldStack)) {
            this.loadFromInfusion();
        }
        this.progress = tag.getInt("progress");
    }

    public ElixirRecipe getElixirRecipe() {
        return this.recipe;
    }

    public int getInfusionTime() {
        return this.bucketInfusionTime;
    }

    public float getProgress() {
        return (float) this.progress / (float) DISTILLING_TIME;
    }

    private void loadFromInfusion() {
        this.recipe = null;
        if (this.infusionBucket.isEmpty() || infusionBucket.getTag() == null) return;
        int infusionTime = this.infusionBucket.getTag().getInt("infusionTime");
        this.bucketInfusionTime = infusionTime;
        if ( this.infusionBucket.getTag() == null || !this.infusionBucket.getTag().contains("ingredients")) {
            this.addInvalidInfusion();
            return;
        }
        ListNBT nbtList = (ListNBT) this.infusionBucket.getTag().getTag("ingredients");
        List<ItemStack> infusionIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbtList.size(); i++) {
            infusionIngredients.add(new ItemStack(nbtList.getCompound(i)));
        }
        List<IAspectType> infusionAspects = this.getInfusionAspects(infusionIngredients);
        ElixirRecipe recipe = ElixirRecipes.getFromAspects(infusionAspects);
        this.recipe = recipe;
        if (recipe == null || infusionTime < recipe.idealInfusionTime - recipe.infusionTimeVariation || infusionTime > recipe.idealInfusionTime + recipe.infusionTimeVariation) {
            this.addInvalidInfusion();
            return;
        }
        List<Aspect> infusionItemAspects = this.getInfusionItemAspects(infusionIngredients);
        int totalAmount = Amounts.VERY_LOW; //Base amount
        int strengthAspectAmount = 0;
        int durationAspectAmount = 0;
        for (Aspect a : infusionItemAspects) {
            totalAmount += a.amount;
            if (recipe.strengthAspect != null && a.type == recipe.strengthAspect)
                strengthAspectAmount += a.amount;
            if (recipe.durationAspect != null && a.type == recipe.durationAspect)
                durationAspectAmount += a.amount;
        }
        int recipeByariis = 0;
        for (IAspectType a : recipe.aspects) {
            if (a == AspectRegistry.BYARIIS) {
                recipeByariis++;
            }
        }
        this.producableAmount = totalAmount;
        boolean isPositive = true;
        for (IAspectType a : infusionAspects) {
            if (a == AspectRegistry.BYARIIS) {
                if (recipeByariis <= 0) {
                    isPositive = !isPositive;
                } else {
                    recipeByariis--;
                }
            }
        }
        this.producableElixir = isPositive ? recipe.positiveElixir : recipe.negativeElixir;
        float relStrengthAmount = strengthAspectAmount / (float)Amounts.MAX_ASPECT_AMOUNT;
        float relDurationAmount = durationAspectAmount / (float)Amounts.MAX_ASPECT_AMOUNT;
        this.producableStrength = MathHelper.floor(relStrengthAmount * ElixirEffect.VIAL_INFUSION_MAX_POTENCY);
        if (isPositive) {
            this.producableDuration = recipe.baseDuration + MathHelper.floor(recipe.durationModifier * relDurationAmount);
        } else {
            this.producableDuration = recipe.negativeBaseDuration + MathHelper.floor(recipe.negativeDurationModifier * relDurationAmount);
        }
    }

    private void addInvalidInfusion() {
        //Invalid recipe or infusion too short or too long
        this.producableElixir = null;
        this.producableAmount = 0;
        this.producableDuration = 0;
        this.producableStrength = 0;
        this.producableItemAspects.clear();
        if (!this.infusionBucket.isEmpty() && this.infusionBucket.getTag() != null && this.infusionBucket.getTag().contains("ingredients")) {
            ListNBT nbtList = (ListNBT) this.infusionBucket.getTag().getTag("ingredients");
            List<ItemStack> infusionIngredients = new ArrayList<ItemStack>();
            for (int i = 0; i < nbtList.size(); i++) {
                infusionIngredients.add(new ItemStack(nbtList.getCompound(i)));
            }
            List<Aspect> infusionAspects = this.getInfusionItemAspects(infusionIngredients);
            for (Aspect aspect : infusionAspects) {
                this.producableItemAspects.add(new Aspect(aspect.type, MathHelper.floor((aspect.amount * (1.0F - ISOLATION_LOSS_MULTIPLIER)) / 3.0F)));
            }
        }
    }

    public List<IAspectType> getInfusionAspects(List<ItemStack> ingredients) {
        List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
        for (ItemStack ingredient : ingredients) {
            ItemAspectContainer container = ItemAspectContainer.fromItem(ingredient, AspectManager.get(this.level));
            for (Aspect aspect : container.getAspects()) {
                infusingAspects.add(aspect.type);
            }
            //infusingAspects.addAll(AspectManager.get(this.world).getDiscoveredAspectTypes(AspectManager.getAspectItem(ingredient), null));
        }
        return infusingAspects;
    }

    private List<Aspect> getInfusionItemAspects(List<ItemStack> ingredients) {
        List<Aspect> infusingItemAspects = new ArrayList<Aspect>();
        for (ItemStack ingredient : ingredients) {
            ItemAspectContainer container = ItemAspectContainer.fromItem(ingredient, AspectManager.get(this.level));
            infusingItemAspects.addAll(container.getAspects());
            //infusingItemAspects.addAll(AspectManager.get(this.world).getDiscoveredAspects(AspectManager.getAspectItem(ingredient), null));
        }
        return infusingItemAspects;
    }

    public boolean isFull() {
        return !this.infusionBucket.isEmpty();
    }

    public boolean hasFinished() {
        return this.progress >= DISTILLING_TIME;
    }

    public boolean hasElixir() {
        return this.producedAmount > 0.0F;
    }

    public boolean isRunning() {
        return this.running;
    }

    /**
     * Creates an item stack with the elixir in the alembic.
     * Vial types: 0 = green, 1 = orange
     *
     * @param vialType
     * @return
     */
    public ItemStack getElixir(int vialType) {
        if (this.isFull() && this.hasFinished()) {
            if (this.producableElixir != null) {
                ItemStack elixir = ItemStack.EMPTY;
                if (this.hasElixir()) {
                    elixir = this.createElixir(this.producableElixir, this.producableStrength, this.producableDuration, vialType);
                }
                this.producedAmount -= AMOUNT_PER_VIAL;
                if (this.producedAmount <= 0.0F || !this.hasElixir()) {
                    this.reset();
                }
                return elixir;
            } else {
                ItemStack aspectVial = ItemStack.EMPTY;
                if (this.producableItemAspects.size() >= 1) {
                    Aspect aspect = this.producableItemAspects.get(0);
                    this.producableItemAspects.remove(0);
                    int removedAmount = aspect.amount;
                    Iterator<Aspect> itemAspectIT = this.producableItemAspects.iterator();
                    while (itemAspectIT.hasNext()) {
                        Aspect currentAspect = itemAspectIT.next();
                        if (currentAspect.type == aspect.type) {
                            removedAmount += currentAspect.amount;
                            itemAspectIT.remove();
                        }
                    }
                    if(removedAmount > Amounts.VIAL) {
                    	this.producableItemAspects.add(new Aspect(aspect.type, removedAmount - Amounts.VIAL));
                    	removedAmount = Amounts.VIAL;
                    }
                    aspectVial = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, vialType);
                    ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(aspectVial);
                    aspectContainer.add(aspect.type, (int) removedAmount);
                }
                if (this.producableItemAspects.size() == 0) {
                    this.reset();
                }
                return aspectVial;
            }
        }
        return null;
    }

    public void reset() {
        this.producableItemAspects.clear();
        this.infusionBucket = ItemStack.EMPTY;
        this.producableAmount = 0;
        this.producableDuration = 0;
        this.producableElixir = null;
        this.producableStrength = 0;
        this.producedAmount = 0;
        this.progress = 0;
        level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    private ItemStack createElixir(ElixirEffect elixir, int strength, int duration, int vialType) {
        return ItemRegistry.ELIXIR.get().getElixirItem(elixir, duration, strength, vialType);
    }
}
