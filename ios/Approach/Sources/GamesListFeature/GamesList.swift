import AssetsLibrary
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
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

extension Game.List: ResourceListItem {
	public var name: String { Strings.Game.titleWithOrdinal(index + 1) }
}

public struct GamesList: Reducer {
	public struct State: Equatable {
		public var series: Series.Summary
		public let seriesHost: League.SeriesHost

		public var list: ResourceList<Game.List, Series.ID>.State

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public let isSeriesSharingEnabled: Bool

		public init(series: Series.Summary, host: League.SeriesHost) {
			self.seriesHost = host
			self.series = series
			self.list = .init(
				features: [.moveable],
				query: series.id,
				listTitle: nil,
				emptyContent: .init(
					image: Asset.Media.EmptyState.games,
					title: Strings.Error.Generic.title,
					action: Strings.Action.reload
				)
			)

			@Dependency(\.featureFlags) var featureFlags
			self.isSeriesSharingEnabled = featureFlags.isEnabled(.sharingSeries)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapGame(Game.ID)
			case didTapShareButton
			case didTapEditButton
			case didTapAddButton

			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableSeries(TaskResult<Series.Edit>)
			case didReorderGames(TaskResult<Never>)
			case didAddGameToSeries(TaskResult<Never>)

			case errors(Errors<ErrorID>.Action)
			case list(ResourceList<Game.List, Series.ID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case sharing(Sharing.State)
			case gameEditor(GamesEditor.State)
			case seriesEditor(SeriesEditor.State)
		}

		public enum Action: Equatable {
			case sharing(Sharing.Action)
			case gameEditor(GamesEditor.Action)
			case seriesEditor(SeriesEditor.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.sharing, action: /Action.sharing) {
				Sharing()
			}
			Scope(state: /State.gameEditor, action: /Action.gameEditor) {
				GamesEditor()
			}
			Scope(state: /State.seriesEditor, action: /Action.seriesEditor) {
				SeriesEditor()
			}
		}
	}

	public enum ErrorID: Hashable {
		case gamesNotFound
		case gameNotFound
		case seriesNotFound
		case gamesNotReordered
		case gameNotAdded
	}

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.games) var games
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: fetchGames(seriesId:))
		}

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapShareButton:
					state.destination = .sharing(.init(dataSource: .series(state.series.id)))
					return .none

				case .didTapAddButton:
					return .run { [seriesId = state.series.id] send in
						try await series.addGamesToSeries(seriesId, 1)
					} catch: { error, send in
						await send(.internal(.didAddGameToSeries(.failure(error))))
					}

				case .didTapEditButton:
					return .run { [id = state.series.id] send in
						await send(.internal(.didLoadEditableSeries(TaskResult {
							try await self.series.edit(id)
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

					return .run { _ in await analytics.resetGameSessionID() }

				case .binding:
					return .none
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

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didMove:
						guard let orderedGames = state.list.resources?.map(\.id) else { return .none }
						return .run { [seriesId = state.series.id] _ in
							try await games.reorderGames(orderedGames, inSeries: seriesId)
						} catch: { error, send in
							await send(.internal(.didReorderGames(.failure(error))))
						}

					case .didEdit, .didDelete, .didTap, .didAddNew, .didTapEmptyStateButton, .didArchive:
						return .none
					}

				case let .destination(.presented(.gameEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishUpdating(series):
						state.series = series.asSummary
						return .none

					case .didFinishArchiving:
						return .run { _ in await dismiss() }

					case .didFinishCreating:
						return .none
					}

				case let .destination(.presented(.sharing(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .didLoadEditableSeries(.failure(error)):
					return state.errors
						.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case .list(.internal), .list(.view),
						.destination(.dismiss),
						.destination(.presented(.gameEditor(.internal))), .destination(.presented(.gameEditor(.view))),
						.destination(.presented(.seriesEditor(.internal))), .destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.sharing(.internal))), .destination(.presented(.sharing(.view))),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}

	private func fetchGames(seriesId: Series.ID) -> AsyncThrowingStream<[Game.List], Error> {
		.init { continuation in
			let task = Task {
				do {
					for try await games in games.seriesGames(forId: seriesId, ordering: .byIndex) {
						continuation.yield(games)
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}
			continuation.onTermination = { _ in task.cancel() }
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
