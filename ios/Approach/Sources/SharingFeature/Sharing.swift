import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ScoreSheetLibrary
import ScoresRepositoryInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct Sharing: Reducer {
	public struct State: Equatable {
		public let dataSource: DataSource

		public var games: IdentifiedArrayOf<Game.Shareable> = []
		public var scores: [Game.ID: ScoredGame] = [:]

		@BindingState public var style: ScoreSheetConfiguration.Style = .default
		@BindingState public var labelPosition: ScoreSheetConfiguration.LabelPosition = .bottom
		@BindingState public var isShowingFrameLabels = true
		@BindingState public var isShowingFrameDetails = true
		@BindingState public var isShowingBowlerName = true
		@BindingState public var isShowingLeagueName = true
		@BindingState public var isShowingSeriesDate = true
		@BindingState public var isShowingAlleyName = true
		@BindingState public var displayScale: CGFloat = .zero

		public var errors: Errors<ErrorID>.State = .init()

		var shareableGames: [ScoredGame] {
			games.compactMap { scores[$0.id] }
		}

		var navigationTitle: String {
			switch dataSource {
			case .series:
				return Strings.Sharing.sharingSeries
			case let .games(games):
				return games.count == 1 ? Strings.Sharing.sharingGame : Strings.Sharing.sharingGames
			}
		}

		var hasAlley: Bool { games.first?.series.alley != nil }

		var configuration: ScoreSheetConfiguration {
			.init(
				style: style,
				labelPosition: labelPosition,
				showFrameLabels: isShowingFrameLabels,
				showFrameDetails: isShowingFrameDetails,
				bowlerName: isShowingBowlerName ? games.first?.bowler.name : nil,
				leagueName: isShowingLeagueName ? games.first?.league.name : nil,
				seriesDate: isShowingSeriesDate ? games.first?.series.date : nil,
				alleyName: isShowingAlleyName ? games.first?.series.alley?.name : nil
			)
		}

		public init(dataSource: DataSource) {
			self.dataSource = dataSource
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case onAppear
			case didFirstAppear
			case didTapShareToStoriesButton
			case didTapShareToOtherButton
			case didTapStyle(ScoreSheetConfiguration.Style)
			case didTapDoneButton
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction { case doNothing }
		@CasePathable public enum InternalAction {
			case didLoadGames(Result<[Game.Shareable], Error>)
			case didLoadScore(ScoredGame)

			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum DataSource: Equatable {
		case games([Game.ID])
		case series(Series.ID)
	}

	public enum ErrorID: Hashable {
		case gamesNotFound
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.games) var games
	@Dependency(\.scores) var scores

	public var body: some ReducerOf<Self> {
		BindingReducer(action: \.view)

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .run { [dataSource = state.dataSource] send in
						await send(.internal(.didLoadGames(Result {
							switch dataSource {
							case let .games(ids):
								return try await games.shareGames(ids)
							case let .series(id):
								return try await games.shareSeries(id)
							}
						})))
					}

				case let .didTapStyle(style):
					state.style = style
					return .none

				case .didTapShareToStoriesButton:
					return .none

				case .didTapShareToOtherButton:
					return .none

				case .didTapDoneButton:
					return .run { _ in await dismiss() }

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadGames(.success(games)):
					state.games = .init(uniqueElements: games)
					return .merge(
						state.games.map { game in
							.run { send in
								for try await score in self.scores.observeScore(for: game.id) {
									await send(.internal(.didLoadScore(score)))
									break
								}
							}
						}
					)

				case let .didLoadScore(score):
					state.scores[score.id] = score
					return .none

				case let .didLoadGames(.failure(error)):
					return state.errors
						.enqueue(.gamesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)):
					return .none

				case .errors(.internal), .errors(.view):
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
