package wilyan_kramer.portable_beacons.common.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class DiffuserTileEntity extends TileEntity implements ITickableTileEntity {
    
    private double range = Config.COMMON.diffuserRange.get();
    private ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private int[] effects = new int[] {-1};
    private int durationLeft = 0;
    private int[] amplifiers = new int[] {-1};

	private Random random = new Random();

	public DiffuserTileEntity() {
		super(TileEntityList.diffuser);
		this.effects = new int[]{-1};
		this.durationLeft = 0;
		this.amplifiers = new int[]{0};
	}
	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		handler.invalidate();
	}
	@Override
	public void tick() {
		if (this.level.getBlockState(this.worldPosition).getValue(BlockStateProperties.TRIGGERED) == true ) {
			if (durationLeft > 0) {
				if (!this.level.isClientSide) {
					applyEffects();
					durationLeft--;
					if (random.nextInt(20) == 0) {
						this.level.playSound(
								null, //target player (on server, this means everyone except this player)
								this.worldPosition, 
								SoundEvents.BREWING_STAND_BREW, 
								SoundCategory.BLOCKS,
								1F, // volume
								0.9F + 0.2F * (float) random.nextGaussian()); // pitch
					}
					if (this.level instanceof ServerWorld) {
						((ServerWorld) this.level).sendParticles(
								ParticleTypes.EFFECT , // the effect type
								(double) this.worldPosition.getX() + 0.5D, // x coord
								(double) this.worldPosition.getY() + 0.5D, // y coord
								(double) this.worldPosition.getZ() + 0.5D, // z coord
								3, // the particle intensity
								0.1D, // particle spread in x-direction
								0.1D, // particle spread in y-direction
								0.1D, // particle spread in z-direction
								0.1D // whether the particles rise up or something
								);
					}
				}	
			}
			else {
				this.effects = new int[] {-1};
				this.durationLeft = 0;
				this.amplifiers = new int[] {-1};
			}
		}
		this.setChanged();
		
	}
	@SuppressWarnings("unused")
	private int getColor() {
		List<EffectInstance> effInstList = new ArrayList<>();
		for (int i = 0; i < this.effects.length; i++) {
			effInstList.add(new EffectInstance(Effect.byId(this.effects[i]), this.durationLeft, this.amplifiers[i]));
		}
		return PotionUtils.getColor(effInstList);
	}
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		itemHandler.deserializeNBT(compound.getCompound("Inventory"));
		effects = compound.getIntArray("Effects");
		durationLeft = compound.getInt("Duration");
		amplifiers = compound.getIntArray("Amplifiers");
		super.load(state, compound);
	}
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Inventory", itemHandler.serializeNBT());
		compound.putIntArray("Effects", effects);
		compound.putInt("Duration", durationLeft);
		compound.putIntArray("Amplifiers", amplifiers);
		return super.save(compound);
	}
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag);
	}
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
	}
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getTag();
		handleUpdateTag(this.level.getBlockState(worldPosition), tag);
		this.level.sendBlockUpdated(worldPosition, this.level.getBlockState(worldPosition), this.level.getBlockState(worldPosition), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
	}
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.put("Inventory", itemHandler.serializeNBT());
		tag.putIntArray("Effects", effects);
		tag.putInt("Duration", durationLeft);
		tag.putIntArray("Amplifiers", amplifiers);
		return super.getUpdateTag();
	}
	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				ItemStack stack = this.getStackInSlot(slot);
				List<EffectInstance> effInstList = EffectHelper.getAllEffects(stack); 
				if (effInstList.size() > 0) {
					effects = new int[effInstList.size()];
					amplifiers = new int[effInstList.size()];
					durationLeft = 0;
					for (int i = 0; i < effInstList.size(); i++) {
						EffectInstance effInst = effInstList.get(i);
						effects[i] = Effect.getId(effInst.getEffect());
						if (effInst.getDuration() > durationLeft)
							durationLeft = effInst.getDuration();
						amplifiers[i] = effInst.getAmplifier();
					}
					this.setStackInSlot(slot, new ItemStack(Items.GLASS_BOTTLE));
				}
				setChanged();
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return (stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE);
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (!isItemValid(slot, stack)) {
					return stack;
				}
				else {
					return super.insertItem(slot, stack, simulate);
				}
			}
		};
	}
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}
	public int getDuration() {
		return this.durationLeft;
	}
	public void setDuration(int duration) {
		this.durationLeft = duration;
	}
	public int[] getEffectIds() {
		return this.effects;
	}
	public void setEffectIds(int[] ids) {
		this.effects = ids;
	}
	public int[] getAmplifiers() {
		return this.amplifiers;
	}
	public void setAmplifiers(int[] amps) {
		this.amplifiers = amps;
	}
	
	private void applyEffects() {
		AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(range);
        List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);

        for(PlayerEntity playerentity : list) {
        	for (int i = 0; i < effects.length; i++) {
        		if (effects[i] > 0 && amplifiers[i] >= 0) {
        			if (Effect.byId(effects[i]) == Effects.NIGHT_VISION) {
            			playerentity.addEffect(new EffectInstance(Effect.byId(effects[i]), 300, amplifiers[i], true, true));
        			}
        			else if (Effect.byId(effects[i]) == Effects.HARM || Effect.byId(effects[i]) == Effects.HEAL) {
        				//do not apply it
        			}
        			else {
        				playerentity.addEffect(new EffectInstance(Effect.byId(effects[i]), 40, amplifiers[i], true, true));
        			}
        		}
        	}
        }
	}
}
