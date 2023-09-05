import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ScoreSheetFeature
import ScoringServiceInterface
import StringsLibrary

public struct Sharing: Reducer {
	public struct State: Equatable {
		public let dataSource: DataSource

		public var games: IdentifiedArrayOf<Game.Shareable> = []
		public var scores: [Game.ID: [ScoreStep]] = [:]

		@BindingState public var style: ShareableScoreSheetConfiguration.Style = .default
		@BindingState public var labelPosition: ShareableScoreSheetConfiguration.LabelPosition = .bottom
		@BindingState public var isShowingFrameLabels = true
		@BindingState public var isShowingFrameDetails = true
		@BindingState public var isShowingBowlerName = true
		@BindingState public var isShowingLeagueName = true
		@BindingState public var isShowingSeriesDate = true
		@BindingState public var isShowingAlleyName = true

		public var errors: Errors<ErrorID>.State = .init()

		var shareableGames: [ShareableScoreSheetView.SteppedGame] {
			games.compactMap {
				guard let steps = scores[$0.id] else { return nil }
				return .init(id: $0.id, index: $0.index, score: $0.score, steps: steps)
			}
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

		var configuration: ShareableScoreSheetConfiguration {
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didFirstAppear
			case didTapShareToStoriesButton
			case didTapShareToOtherButton
			case didTapStyle(ShareableScoreSheetConfiguration.Style)
			case didTapDoneButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadGames(TaskResult<[Game.Shareable]>)
			case didLoadScore(Game.ID, [ScoreStep])

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
	@Dependency(\.scoring) var scoring

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { [dataSource = state.dataSource] send in
						await send(.internal(.didLoadGames(TaskResult {
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
								await send(.internal(.didLoadScore(
									game.id,
									scoring.calculateScoreWithSteps(for: game.frames.map(\.rolls))
								)))
							}
						}
					)

				case let .didLoadScore(gameId, score):
					state.scores[gameId] = score
					return .none

				case let .didLoadGames(.failure(error)):
					return state.errors
						.enqueue(.gamesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
