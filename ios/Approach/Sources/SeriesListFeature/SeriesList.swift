import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import Foundation
import GamesListFeature
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ListContentLibrary
import ModelsLibrary
import SeriesEditorFeature
import SeriesRepositoryInterface
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Series.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .oldestFirst: return Strings.Ordering.oldestFirst
		case .newestFirst: return Strings.Ordering.newestFirst
		case .highestToLowest: return Strings.Ordering.highestToLowest
		case .lowestToHighest: return Strings.Ordering.lowestToHighest
		}
	}
}

// swiftlint:disable:next type_body_length
public struct SeriesList: Reducer {
	public struct State: Equatable {
		public var league: League.SeriesHost
		public var series: IdentifiedArrayOf<Series.List> = []
		public var ordering: Series.Ordering = .newestFirst

		public var seriesToNavigate: Series.ID?

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(league: League.SeriesHost) {
			self.league = league
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapAddButton
			case didTapEditButton
			case didTapSortOrderButton
			case didTapSeries(Series.ID)
			case didSwipeSeries(SwipeAction, Series.ID)
		}

		public enum InternalAction: Equatable {
			case seriesResponse(TaskResult<[Series.List]>)
			case didDeleteSeries(TaskResult<Series.ID>)
			case didLoadEditableSeries(TaskResult<Series.Edit>)
			case didLoadEditableLeague(TaskResult<League.Edit>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		public enum DelegateAction: Equatable {}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton(Series.ID)
		case didTapDismissButton
	}

	public enum ErrorID: Hashable {
		case leagueNotFound
		case seriesNotFound
		case seriesNotLoaded
		case failedToDeleteSeries
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case seriesEditor(SeriesEditor.State)
			case leagueEditor(LeagueEditor.State)
			case games(GamesList.State)
			case sortOrder(SortOrderLibrary.SortOrder<Series.Ordering>.State)
			case alert(AlertState<AlertAction>)
		}

		public enum Action: Equatable {
			case seriesEditor(SeriesEditor.Action)
			case leagueEditor(LeagueEditor.Action)
			case games(GamesList.Action)
			case sortOrder(SortOrderLibrary.SortOrder<Series.Ordering>.Action)
			case alert(AlertAction)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.leagueEditor, action: /Action.leagueEditor) {
				LeagueEditor()
			}
			Scope(state: /State.seriesEditor, action: /Action.seriesEditor) {
				SeriesEditor()
			}
			Scope(state: /State.games, action: /Action.games) {
				GamesList()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public init() {}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.leagues) var leagues
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return startObservingSeries(forLeague: state.league.id, orderedBy: state.ordering)

				case let .didTapSeries(id):
					if let series = state.series[id: id] {
						state.destination = .games(.init(series: series.asSummary, host: state.league))
					}
					return .none

				case .didTapEditButton:
					return .run { [id = state.league.id] send in
						await send(.internal(.didLoadEditableLeague(TaskResult {
							try await leagues.edit(id)
						})))
					}

				case .didTapAddButton:
					state.destination = .seriesEditor(.init(
						value: .create(.default(withId: uuid(), onDate: calendar.startOfDay(for: date()), inLeague: state.league)),
						inLeague: state.league
					))
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none

				case let .didSwipeSeries(.delete, id):
					guard let series = state.series[id: id] else { return .none }
					state.destination = .alert(.init(
						title: TextState(Strings.Form.Prompt.delete(series.date.longFormat)),
						primaryButton: .destructive(
							TextState(Strings.Action.delete),
							action: .send(.didTapDeleteButton(series.id))
						),
						secondaryButton: .cancel(
							TextState(Strings.Action.cancel),
							action: .send(.didTapDismissButton)
						)
					))
					return .none

				case let .didSwipeSeries(.edit, id):
					return .run { send in
						await send(.internal(.didLoadEditableSeries(TaskResult {
							try await self.series.edit(id)
						})))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .seriesResponse(.success(series)):
					state.series = .init(uniqueElements: series)
					if let seriesToNavigate = state.seriesToNavigate {
						if let destination = state.series[id: seriesToNavigate] {
							state.seriesToNavigate = nil
							state.destination = .games(.init(series: destination.asSummary, host: state.league))
						} else {
							return .send(.internal(.didLoadEditableSeries(.failure(SeriesListError.seriesNotFound(seriesToNavigate)))))
						}
					}
					return .none

				case let .didLoadEditableSeries(.success(series)):
					state.destination = .seriesEditor(.init(value: .edit(series), inLeague: state.league))
					return .none

				case let .didLoadEditableLeague(.success(league)):
					state.destination = .leagueEditor(.init(value: .edit(league)))
					return .none

				case .didDeleteSeries(.success):
					return .none

				case let .seriesResponse(.failure(error)):
					return state.errors
						.enqueue(.seriesNotLoaded, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didDeleteSeries(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteSeries, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableSeries(.failure(error)):
					return state.errors
						.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableLeague(.failure(error)):
					return state.errors
						.enqueue(.leagueNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						if let series = state.series[id: created.id] {
							state.destination = .games(.init(series: series.asSummary, host: state.league))
						} else {
							state.seriesToNavigate = created.id
						}
						return .none

					case .didFinishDeleting, .didFinishUpdating:
						return .none
					}

				case let .destination(.presented(.leagueEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishUpdating(league):
						state.league = league.asSeriesHost
						return .none

					case .didFinishDeleting:
						return .run { _ in await dismiss() }

					case .didFinishCreating:
						return .none
					}

				case let .destination(.presented(.games(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.alert(.didTapDeleteButton(id)))):
					return .run { send in
						try await self.series.delete(id)
						await send(.internal(.didDeleteSeries(.success(id))))
					} catch: { error, send in
						await send(.internal(.didDeleteSeries(.failure(error))))
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return startObservingSeries(forLeague: state.league.id, orderedBy: state.ordering)
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.seriesEditor(.view))), .destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.leagueEditor(.view))), .destination(.presented(.leagueEditor(.internal))),
						.destination(.presented(.games(.view))), .destination(.presented(.games(.internal))),
						.destination(.presented(.alert(.didTapDismissButton))),
						.destination(.presented(.sortOrder(.internal))), .destination(.presented(.sortOrder(.view))),
						.errors(.view),
						.errors(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapSeries):
				return Analytics.Series.Viewed()
			case .internal(.didDeleteSeries(.success)):
				return Analytics.Series.Deleted()
			default:
				return nil
			}
		}
	}

	private func startObservingSeries(forLeague: League.ID, orderedBy: Series.Ordering) -> Effect<Action> {
		return .run { send in
			for try await series in self.series.list(bowledIn: forLeague, orderedBy: orderedBy) {
				await send(.internal(.seriesResponse(.success(series))))
			}
		} catch: { error, send in
			await send(.internal(.seriesResponse(.failure(error))))
		}
	}
}

extension ListErrorContent {
	static let createError = Self(
		title: Strings.Series.Error.FailedToCreate.title,
		message: Strings.Series.Error.FailedToCreate.message,
		action: Strings.Action.tryAgain
	)
}

public enum SeriesListError: LocalizedError {
	case seriesNotFound(Series.ID)

	public var errorDescription: String? {
		switch self {
		case let .seriesNotFound(id):
			return "Could not find Series with ID '\(id)'"
		}
	}
}
