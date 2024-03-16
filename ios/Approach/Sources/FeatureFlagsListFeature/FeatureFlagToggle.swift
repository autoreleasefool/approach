import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import SwiftUI

@Reducer
public struct FeatureFlagToggle: Reducer {
	@ObservableState
	public struct State: Equatable, Identifiable {
		public var flag: FeatureFlagItem
		public var id: String { flag.flag.id }

		init(flag: FeatureFlagItem) {
			self.flag = flag
		}
	}

	public enum Action: BindableAction {
		case binding(BindingAction<State>)
	}

	public var body: some ReducerOf<Self> {
		BindingReducer()
	}
}

public struct FeatureFlagToggleView: View {
	@Bindable public var store: StoreOf<FeatureFlagToggle>

	public var body: some View {
		Toggle(
			store.flag.flag.name,
			isOn: $store.flag.enabled
		).disabled(!store.flag.flag.isOverridable)
	}
}
