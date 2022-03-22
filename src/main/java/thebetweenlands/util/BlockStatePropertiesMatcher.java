package thebetweenlands.util;

import com.google.common.base.Predicate;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;

public class BlockStatePropertiesMatcher implements Predicate<BlockState> {
	private final BlockStateMatcher matcher;

	private BlockStatePropertiesMatcher(BlockStateMatcher matcher) {
		this.matcher = matcher;
	}

	public static BlockStatePropertiesMatcher forBlockState(BlockState state, IProperty<?>... properties) {
		BlockStateMatcher matcher = BlockStateMatcher.forBlock(state.getBlock());
		if(properties == null || properties.length == 0)
			properties = state.getBlock().getBlockState().getProperties().toArray(new IProperty<?>[0]);
		for(IProperty<?> property : properties) {
			Object value = state.getValue(property);
			matcher.where(property, (obj) -> (obj == null && value == null) || (obj != null && obj.equals(value)));
		}
		return new BlockStatePropertiesMatcher(matcher);
	}

	@Override
	public boolean apply(BlockState input) {
		return this.matcher.apply(input);
	}
}
