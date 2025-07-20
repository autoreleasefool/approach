import ComposableArchitecture
import FeatureActionLibrary
import SettingsFeature
import SwiftUI

@Reducer
public struct SettingsTabContent: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var settings = Settings.State()

		public static var `default`: Self { Self() }
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case settings(Settings.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.settings, action: \.internal.settings) {
			Settings()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing: return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .settings(.delegate(.doNothing)):
					return .none

				case .settings(.internal), .settings(.view), .settings(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct SettingsTabContentView: View {
	public let store: StoreOf<SettingsTabContent>

	init(store: StoreOf<SettingsTabContent>) {
		self.store = store
	}

	public var body: some View {
		NavigationStack {
			SettingsView(
				store: store.scope(state: \.settings, action: \.internal.settings)
			)
		}
	}
}
