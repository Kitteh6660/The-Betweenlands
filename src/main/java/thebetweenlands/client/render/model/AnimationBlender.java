package thebetweenlands.client.render.model;

import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnimationBlender<T extends MowzieModelBase> {
	private class State {
		private final Consumer<T> animator;
		private final Supplier<Float> weight;

		private float currentWeight;

		private State(Consumer<T> animator, Supplier<Float> weight) {
			this.animator = animator;
			this.weight = weight;
		}

		private void update(AnimationBlender<T> blender, T model, boolean reset) {
			if(reset) {
				for(int i = 0; i < model.boxList.size(); i++) {
					ModelRenderer part = model.boxList.get(i);
					part.xRot = Float.NaN;
					part.yRot = Float.NaN;
					part.zRot = Float.NaN;
				}
			}

			this.animator.accept(model);

			for(int i = 0; i < model.boxList.size(); i++) {
				ModelRenderer part = model.boxList.get(i);

				if(Float.isFinite(part.xRot)) {
					blender.rotX[i] += this.currentWeight * part.xRot;
				}

				if(Float.isFinite(part.yRot)) {
					blender.rotY[i] += this.currentWeight * part.yRot;
				}

				if(Float.isFinite(part.zRot)) {
					blender.rotZ[i] += this.currentWeight * part.zRot;
				}
			}
		}
	}

	private final T model;

	private final State baseState;
	private final List<State> states = new ArrayList<>();

	private final float[] rotX;
	private final float[] rotY;
	private final float[] rotZ;

	public AnimationBlender(T model) {
		this.model = model;
		this.states.add(this.baseState = new State(m -> {}, () -> 1.0f));
		this.rotX = new float[this.model.boxList.size()];
		this.rotY = new float[this.model.boxList.size()];
		this.rotZ = new float[this.model.boxList.size()];
	}

	/**
	 * @param animator The animator that set's the model part rotations for this state
	 * @param weight The state's weight
	 */
	public void addState(Consumer<T> animator, Supplier<Float> weight) {
		this.states.add(new State(animator, weight));
	}

	/**
	 * Set's the angles as a weighted combination of each state's rotations.
	 * If all state weights add up to 1 this is the same as an interpolation between all states.
	 * @param useExistingAngles Whether currently already existing rotations should be added. If false all rotations are initialised with 0.
	 */
	public void setAngles(boolean useExistingAngles) {
		for(int i = 0; i < this.model.boxList.size(); i++) {
			this.rotX[i] = this.rotY[i] = this.rotZ[i] = 0;
		}

		for(State state : this.states) {
			if(useExistingAngles || state != this.baseState) {
				state.currentWeight = state.weight.get();
				state.update(this, this.model, state != this.baseState);
			}
		}

		for(int i = 0; i < this.model.boxList.size(); i++) {
			ModelRenderer part = model.boxList.get(i);

			part.xRot = this.rotX[i];
			part.yRot = this.rotY[i];
			part.zRot = this.rotZ[i];
		}
	}
}
