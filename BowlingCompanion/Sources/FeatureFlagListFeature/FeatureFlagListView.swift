import ComposableArchitecture
import FeatureFlagLibrary
import StringsLibrary
import SwiftUI

public struct FeatureFlagListView: View {
	let store: StoreOf<FeatureFlagList>

	struct ViewState: Equatable {
		let featureFlags: [FeatureFlagList.FeatureFlagItem]

		init(state: FeatureFlagList.State) {
			self.featureFlags = state.featureFlags
		}
	}

	enum ViewAction {
		case subscribeToFlags
		case toggle(flag: FeatureFlag)
		case resetOverridesButtonTapped
	}

	public init(store: StoreOf<FeatureFlagList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: FeatureFlagList.Action.init) { viewStore in
			List {
				Section {
					Button(Strings.Settings.FeatureFlags.reset) { viewStore.send(.resetOverridesButtonTapped) }
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

extension FeatureFlagList.Action {
	init(action: FeatureFlagListView.ViewAction) {
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
