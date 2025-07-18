import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import GamesEditorFeature
import GamesRepositoryInterface
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SeriesRepositoryInterface
import SharingFeature
import StringsLibrary
import SwiftUI
import TipsLibrary
import TipsServiceInterface

extension Game.List: ResourceListItem {
	public var name: String { Strings.Game.titleWithOrdinal(index + 1) }
}

@Reducer
public struct GamesList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var series: Series.GameHost
		public let seriesHost: League.SeriesHost

		public var list: ResourceList<Game.List, Series.ID>.State

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public var isShowingArchiveTip: Bool

		public init(series: Series.GameHost, host: League.SeriesHost) {
			self.seriesHost = host
			self.series = series
			self.list = .init(
				features: [.moveable, .swipeToArchive],
				query: SharedReader(value: series.id),
				listTitle: nil,
				emptyContent: .init(
					image: Asset.Media.EmptyState.games,
					title: Strings.Error.Generic.title,
					action: Strings.Action.reload
				)
			)

			@Dependency(TipsService.self) var tips
			self.isShowingArchiveTip = tips.shouldShow(tipFor: .gameArchiveTip)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapGame(Game.ID)
			case didTapShareButton
			case didTapEditButton
			case didTapAddButton
			case didTapArchiveTipDismissButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didLoadEditableSeries(Result<Series.Edit, Error>)
			case didReorderGames(Result<Never, Error>)
			case didAddGameToSeries(Result<Never, Error>)
			case didArchiveGame(Result<Never, Error>)

			case errors(Errors<ErrorID>.Action)
			case list(ResourceList<Game.List, Series.ID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}
		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case sharing(Sharing)
		case gameEditor(GamesEditor)
		case seriesEditor(SeriesEditor)
	}

	public enum ErrorID: Hashable, Sendable {
		case gamesNotFound
		case gameNotFound
		case seriesNotFound
		case gamesNotReordered
		case gameNotAdded
		case failedToArchiveGame
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.gameAnalytics) var gameAnalytics
	@Dependency(GamesRepository.self) var games
	@Dependency(SeriesRepository.self) var series
	@Dependency(TipsService.self) var tips
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: \.internal.list) {
			ResourceList { @Sendable in
				games.seriesGames(forId: $0, ordering: .byIndex)
			}
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapShareButton:
					state.destination = .sharing(.init(source: .series(state.series.id)))
					return .none

				case .didTapArchiveTipDismissButton:
					state.isShowingArchiveTip = false
					return .run { _ in await tips.hide(tipFor: .gameArchiveTip) }

				case .didTapAddButton:
					return .run { [seriesId = state.series.id] _ in
						try await series.addGamesToSeries(seriesId, 1)
					} catch: { error, send in
						await send(.internal(.didAddGameToSeries(.failure(error))))
					}

				case .didTapEditButton:
					return .run { [id = state.series.id] send in
						await send(.internal(.didLoadEditableSeries(Result {
							try await series.edit(id)
						})))
					}

				case let .didTapGame(id):
					guard let games = state.list.resources else {
						return state.errors
							.enqueue(
								.gamesNotFound,
								thrownError: GamesListError.gamesNotFound,
								toastMessage: Strings.Error.Toast.dataNotFound
							)
							.map { .internal(.errors($0)) }
					}

					guard let game = games[id: id] else {
						return state.errors
							.enqueue(
								.gameNotFound,
								thrownError: GamesListError.gameNotFound(id),
								toastMessage: Strings.Error.Toast.dataNotFound
							)
							.map { .internal(.errors($0)) }
					}

					state.destination = .gameEditor(.init(
						bowlerIds: [game.bowlerId],
						bowlerGameIds: [game.bowlerId: games.map(\.id)],
						initialBowlerId: game.bowlerId,
						initialGameId: id
					))

					return .run { _ in await gameAnalytics.resetGameSessionID() }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableSeries(.success(series)):
					state.destination = .seriesEditor(.init(value: .edit(series), inLeague: state.seriesHost))
					return .none

				case let .didReorderGames(.failure(error)):
					return state.errors
						.enqueue(.gamesNotReordered, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didAddGameToSeries(.failure(error)):
					return state.errors
						.enqueue(.gameNotAdded, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didArchiveGame(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didMove:
						guard let orderedGames = state.list.resources?.map(\.id) else { return .none }
						return .run { [seriesId = state.series.id] _ in
							try await games.reorderGames(orderedGames, inSeries: seriesId)
						} catch: { error, send in
							await send(.internal(.didReorderGames(.failure(error))))
						}

					case let .didArchive(game):
						return .run { _ in
							try await games.archive(game.id)
						} catch: { error, send in
							await send(.internal(.didArchiveGame(.failure(error))))
						}

					case .didEdit, .didDelete, .didTap, .didAddNew, .didTapEmptyStateButton:
						return .none
					}

				case .destination(.presented(.gameEditor(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishUpdating(series):
						state.series = series.asGameHost
						return .none

					case .didFinishArchiving:
						return .run { _ in await dismiss() }

					case .didFinishCreating:
						return .none
					}

				case .destination(.presented(.sharing(.delegate(.doNothing)))):
					return .none

				case .errors(.delegate(.doNothing)):
					return .none

				case let .didLoadEditableSeries(.failure(error)):
					return state.errors
						.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case .list(.internal), .list(.view),
						.destination(.dismiss),
						.destination(.presented(.gameEditor(.internal))),
						.destination(.presented(.gameEditor(.view))),
						.destination(.presented(.gameEditor(.binding))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.destination(.presented(.sharing(.internal))),
						.destination(.presented(.sharing(.view))),
						.destination(.presented(.sharing(.binding))),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapShareButton): return Analytics.Series.Shared()
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didReorderGames(.failure(error))),
				let .internal(.didAddGameToSeries(.failure(error))),
				let .internal(.didArchiveGame(.failure(error))),
				let .internal(.didLoadEditableSeries(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

public enum GamesListError: LocalizedError {
	case gameNotFound(Game.ID)
	case gamesNotFound

	public var errorDescription: String? {
		switch self {
		case let .gameNotFound(id):
			return "Could not find Game with ID '\(id)'"
		case .gamesNotFound:
			return "Games were not loaded in List"
		}
	}
}

extension Tip {
	static let gameArchiveTip = Tip(
		id: "Games.List.archive",
		title: Strings.Game.List.Footer.ArchiveTip.title,
		message: Strings.Game.List.Footer.ArchiveTip.message
	)
}
