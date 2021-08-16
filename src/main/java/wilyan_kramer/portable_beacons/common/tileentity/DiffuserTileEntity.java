package wilyan_kramer.portable_beacons.common.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class DiffuserTileEntity extends TileEntity implements ITickableTileEntity, INameable {
    
    private double range = Config.COMMON.diffuserRange.get();
    private ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private int[] effects;
    private int durationLeft;
    private int[] amplifiers;
    private ITextComponent name;

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
		//if ( ) {
		if (durationLeft > 0) {
			if (this.level.getBlockState(this.worldPosition).getValue(BlockStateProperties.TRIGGERED) == true) {
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
								0.1D // particles rising speed or something
								);
					}
				}	
			}
		}
		else {
			this.effects = new int[] {-1,-1,-1,-1};
			this.durationLeft = 0;
			this.amplifiers = new int[] {0,0,0,0};
		}
		//}
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
		if (compound.contains("CustomName", 8)) {
	         this.name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
	      }
		super.load(state, compound);
	}
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Inventory", itemHandler.serializeNBT());
		compound.putIntArray("Effects", effects);
		compound.putInt("Duration", durationLeft);
		compound.putIntArray("Amplifiers", amplifiers);
		if (this.name != null) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
	    }
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
				if (slot == 0) {
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
	public final IIntArray dataAccess = new IIntArray() {
	      public int get(int index) {
	         switch(index) {
	         case 0:
	            return DiffuserTileEntity.this.durationLeft;
	         case 1:
	        	 if (DiffuserTileEntity.this.effects.length > 0) {
	        		 return DiffuserTileEntity.this.effects[0];
	        	 }
	        	 else {
	        		 return -1;
	        	 }
	         case 2:
	        	 if (DiffuserTileEntity.this.effects.length > 1) {
	        		 return DiffuserTileEntity.this.effects[1];
	        	 }
	        	 else {
	        		 return -1;
	        	 }
	         case 3:
	        	 if (DiffuserTileEntity.this.effects.length > 2) {
	        		 return DiffuserTileEntity.this.effects[2];
	        	 }
	        	 else {
	        		 return -1;
	        	 }
	         case 4:
	        	 if (DiffuserTileEntity.this.effects.length > 3) {
	        		 return DiffuserTileEntity.this.effects[3];
	        	 }
	        	 else {
	        		 return -1;
	        	 }
	         case 5:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 0) {
	        		 return DiffuserTileEntity.this.amplifiers[0];
	        	 }
	        	 else {
	        		 return 0;
	        	 }
	         case 6:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 1) {
	        		 return DiffuserTileEntity.this.amplifiers[1];
	        	 }
	        	 else {
	        		 return 0;
	        	 }
	         case 7:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 2) {
	        		 return DiffuserTileEntity.this.amplifiers[2];
	        	 }
	        	 else {
	        		 return 0;
	        	 }
	         case 8:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 3) {
	        		 return DiffuserTileEntity.this.amplifiers[3];
	        	 }
	        	 else {
	        		 return 0;
	        	 }
	         default:
	            return 0;
	         }
	      }

	      public void set(int index, int value) {
	         switch(index) {
	         case 0:
	            DiffuserTileEntity.this.durationLeft = value;
	            break;
	         case 1:
	        	 if (DiffuserTileEntity.this.effects.length > 0) {
	        		 DiffuserTileEntity.this.effects[0] = value;
	        	 }
	            break;
	         case 2:
	        	if (DiffuserTileEntity.this.effects.length > 1) {
	        		DiffuserTileEntity.this.effects[1] = value;
	        	}
	            break;
	         case 3:
	        	 if (DiffuserTileEntity.this.effects.length > 2) {
	        		 DiffuserTileEntity.this.effects[2] = value;
	        	 }
	        	 break;
	         case 4:
	        	 if (DiffuserTileEntity.this.effects.length > 3) {
	        		 DiffuserTileEntity.this.effects[3] = value;
	        	 }
	        	 break;
	         case 5:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 0) {
	        		 DiffuserTileEntity.this.amplifiers[0] = value;
	        	 }
	        	 break;
	         case 6:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 1) {
	        		 DiffuserTileEntity.this.amplifiers[1] = value;
	        	 }
	        	 break;
	         case 7:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 2) {
	        		 DiffuserTileEntity.this.amplifiers[2] = value;
	        	 }
	        	 break;	 
	         case 8:
	        	 if (DiffuserTileEntity.this.amplifiers.length > 3) {
	        		 DiffuserTileEntity.this.amplifiers[3] = value;
	        	 }
	        	 break;
	         }

	      }

	      public int getCount() {
	         return 9;
	      }
	   };
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
		List<LivingEntity> list;
		if (Config.COMMON.diffuserMobs.get()) {
			list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
		}
		else {
			list = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);

		}
        for(LivingEntity entity : list) {
        	for (int i = 0; i < effects.length; i++) {
        		if (effects[i] > 0 && amplifiers[i] >= 0) {
        			if (Effect.byId(effects[i]) == Effects.NIGHT_VISION) {
            			entity.addEffect(new EffectInstance(Effect.byId(effects[i]), 300, amplifiers[i], true, true));
        			}
        			else if (Effect.byId(effects[i]) == Effects.HARM || Effect.byId(effects[i]) == Effects.HEAL) {
        				//do not apply it
        			}
        			else {
        				entity.addEffect(new EffectInstance(Effect.byId(effects[i]), 40, amplifiers[i], true, true));
        			}
        		}
        	}
        }
	}
	@Override
	public ITextComponent getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}
	private ITextComponent getDefaultName() {
		return new TranslationTextComponent("block.portable_beacons.diffuser");
	}
	@Override
	public boolean hasCustomName() {
		return this.getCustomName() != null;
	}
	@Override
	public ITextComponent getDisplayName() {
		return this.getName();
	}
	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return this.name;
	}
	public void setCustomName(ITextComponent name) {
	   this.name = name;
	}
}
