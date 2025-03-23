import AchievementsLibrary
import AchievementsRepositoryInterface
import Algorithms
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
		public var list: [GridRow] = []

		public var errors: Errors<ErrorID>.State = .init()

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didStartTask
			case didTapAchievement(String)
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

	public struct GridRow: Identifiable, Equatable {
		let first: Achievement.List
		let second: Achievement.List?
		let third: Achievement.List?

		public var id: String { "\(first.id)-\(second?.id ?? "")-\(third?.id ?? "")" }
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
					return .run { send in
						for try await earned in achievements.list() {
							await send(.internal(.didLoadAchievements(.success(earned))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadAchievements(.failure(error))))
					}

				case let .didTapAchievement(id):
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadAchievements(.success(achievements)):
					let earnedAchievements = Dictionary(
						uniqueKeysWithValues: achievements.map { ($0.title, $0) }
					)
					let achievementsList = EarnableAchievements.allCases
						.filter {
							$0.isEnabled && ($0.isVisibleBeforeEarned || earnedAchievements[$0.title] != nil)
						}
						.chunks(ofCount: 3)
						.map {
							let first = resolveAchievementCount(achievement: $0[0], earnedAchievements: earnedAchievements)
							let second = $0.indices.contains(1)
								? resolveAchievementCount(achievement: $0[1], earnedAchievements: earnedAchievements)
								: nil
							let third = $0.indices.contains(2)
								? resolveAchievementCount(achievement: $0[2], earnedAchievements: earnedAchievements)
								: nil

							return GridRow(
								first: first,
								second: second,
								third: third
							)
						}

					state.list = achievementsList
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

	private func resolveAchievementCount(
		achievement: EarnableAchievement.Type,
		earnedAchievements: [String: Achievement.List]
	) -> Achievement.List {
		earnedAchievements[achievement.title] ?? Achievement.List(title: achievement.title, firstEarnedAt: nil, count: 0)
	}
}

// MARK: - View

@ViewAction(for: AchievementsList.self)
public struct AchievementsListView: View {
	public let store: StoreOf<AchievementsList>
	let columns = [
		GridItem(.flexible(), spacing: .standardSpacing),
		GridItem(.flexible(), spacing: .standardSpacing),
		GridItem(.flexible(), spacing: .standardSpacing),
	]

	public init(store: StoreOf<AchievementsList>) {
		self.store = store
	}

	public var body: some View {
		ScrollView {
			LazyVGrid(columns: columns) {
				ForEach(store.list) { row in
					achievementListItem(for: row.first)

					if let second = row.second {
						achievementListItem(for: second)
					}

					if let third = row.third {
						achievementListItem(for: third)
					}
				}
			}
			.padding(.horizontal, .standardSpacing)
		}
		.onAppear { send(.onAppear) }
		.task { await send(.didStartTask).finish() }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}

	private func achievementListItem(for achievement: Achievement.List) -> some View {
		Button { send(.didTapAchievement(achievement.id)) } label: {
			AchievementListItem(achievement: achievement, isMoveable: false)
		}
		.buttonStyle(.plain)
	}
}
