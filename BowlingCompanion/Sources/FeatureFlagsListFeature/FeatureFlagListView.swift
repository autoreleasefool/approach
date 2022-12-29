import ComposableArchitecture
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI

public struct FeatureFlagsListView: View {
	let store: StoreOf<FeatureFlagsList>

	struct ViewState: Equatable {
		let featureFlags: [FeatureFlagsList.FeatureFlagItem]

		init(state: FeatureFlagsList.State) {
			self.featureFlags = state.featureFlags
		}
	}

	enum ViewAction {
		case subscribeToFlags
		case toggle(flag: FeatureFlag)
		case resetOverridesButtonTapped
	}

	public init(store: StoreOf<FeatureFlagsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: FeatureFlagsList.Action.init) { viewStore in
			List {
				Section {
					Button(Strings.Action.reset) { viewStore.send(.resetOverridesButtonTapped) }
				}

				Section(Strings.Settings.FeatureFlags.title) {
					ForEach(viewStore.featureFlags, id: \.flag.id) { item in
						Toggle(
							item.flag.name,
							isOn: viewStore.binding(
								get: { _ in item.enabled },
								send: { _ in ViewAction.toggle(flag: item.flag) }
							)
						).disabled(!item.flag.isOverridable)
					}
				}
			}
			.task { await viewStore.send(.subscribeToFlags).finish() }
		}
	}
}

extension FeatureFlagsList.Action {
	init(action: FeatureFlagsListView.ViewAction) {
		switch action {
		case .subscribeToFlags:
			self = .subscribeToFlags
		case let .toggle(featureFlag):
			self = .toggle(featureFlag)
		case .resetOverridesButtonTapped:
			self = .resetOverridesButtonTapped
		}
	}
}
