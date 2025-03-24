import AchievementsLibrary
import AchievementsServiceInterface
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct AchievementDetails: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let achievement: Achievement.List
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none
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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: AchievementDetails.self)
struct AchievementDetailsView: View {
	let store: StoreOf<AchievementDetails>

	init(store: StoreOf<AchievementDetails>) {
		self.store = store
	}

	var body: some View {
		ScrollView {
			InteractiveAchievement(store.achievement.icon, isEnabled: true)
		}
		.onAppear { send(.onAppear) }
	}
}
