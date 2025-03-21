import AchievementsLibrary
import AchievementsRepositoryInterface
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct AchievementsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var list: IdentifiedArrayOf<Achievement.List> = []

		public var errors: Errors<ErrorID>.State = .init()

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didStartTask
			case didTapAchievement(Achievement.ID)
		}
		@CasePathable
		public enum Internal {
			case didLoadAchievements(Result<[Achievement.List], Error>)
			case errors(Errors<ErrorID>.Action)
		}
		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum ErrorID: Hashable {
		case failedToLoadAchievements
	}

	public init() {}

	@Dependency(AchievementsRepository.self) var achievements

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didStartTask:
					return .none

				case let .didTapAchievement(id):
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadAchievements(.success(achievements)):
					state.list = IdentifiedArrayOf(uniqueElements: achievements)
					return .none

				case let .didLoadAchievements(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadAchievements, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear):
				return Analytics.Achievement.ListViewed()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadAchievements(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: AchievementsList.self)
public struct AchievementsListView: View {
	public let store: StoreOf<AchievementsList>

	public init(store: StoreOf<AchievementsList>) {
		self.store = store
	}

	public var body: some View {
		Text("Hello, Achievements!")
			.onAppear { send(.onAppear) }
			.task { await send(.didStartTask).finish() }
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}
}
