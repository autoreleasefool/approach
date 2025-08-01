import AnalyticsServiceInterface
import ComposableArchitecture
import DatabaseMockingServiceInterface
import FeatureActionLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct DeveloperOptionsSettings: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapPopulateDatabase
			case didTapResetDatabase
			case didTapForceCrash
		}

		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(\.crash) var crash
	@Dependency(DatabaseMockingService.self) var databaseMocking

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapPopulateDatabase:
					return .run { _ in try await databaseMocking.mockDatabase() }

				case .didTapResetDatabase:
					fatalError()

				case .didTapForceCrash:
					return .run { _ in crash() }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .doNothing:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: DeveloperOptionsSettings.self)
struct DeveloperOptionsSettingsView: View {
	let store: StoreOf<DeveloperOptionsSettings>

	init(store: StoreOf<DeveloperOptionsSettings>) {
		self.store = store
	}

	var body: some View {
		List {
			Section {
				Button(Strings.Settings.DeveloperOptions.populateDatabase) {
					send(.didTapPopulateDatabase)
				}

				Button(Strings.Settings.DeveloperOptions.resetDatabase) {
					send(.didTapResetDatabase)
				}
			}

			Section {
				Button(Strings.Settings.DeveloperOptions.forceCrash) {
					send(.didTapForceCrash)
				}
			}
		}
	}
}
