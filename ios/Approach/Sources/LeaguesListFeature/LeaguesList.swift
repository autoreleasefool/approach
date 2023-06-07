import ComposableArchitecture
import FeatureActionLibrary
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SeriesListFeature
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension League.List: ResourceListItem {}

extension League.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

public struct LeaguesList: Reducer {
	public struct State: Equatable {
		public let bowler: Bowler.Summary

		public var list: ResourceList<League.List, League.List.FetchRequest>.State

		public var ordering: League.Ordering = .byRecentlyUsed
		public var filter: League.List.FetchRequest.Filter

		@PresentationState public var destination: Destination.State?

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
			self.filter = .init(bowler: bowler.id)
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.leagues) var leagues: LeaguesRepository
						try await leagues.delete($0.id)
					}),
				],
				query: .init(
					filter: filter,
					ordering: ordering
				),
				listTitle: Strings.League.List.title,
				emptyContent: .init(
					image: .emptyLeagues,
					title: Strings.League.Error.Empty.title,
					message: Strings.League.Error.Empty.message,
					action: Strings.League.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapLeague(id: League.ID)
			case didTapFilterButton
			case didTapSortOrderButton
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableLeague(League.Edit)
			case didLoadSeriesLeague(League.SeriesHost)
			case list(ResourceList<League.List, League.List.FetchRequest>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(LeagueEditor.State)
			case filters(LeaguesFilter.State)
			case series(SeriesList.State)
			case sortOrder(SortOrder<League.Ordering>.State)
		}

		public enum Action: Equatable {
			case editor(LeagueEditor.Action)
			case filters(LeaguesFilter.Action)
			case series(SeriesList.Action)
			case sortOrder(SortOrder<League.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				LeagueEditor()
			}
			Scope(state: /State.filters, action: /Action.filters) {
				LeaguesFilter()
			}
			Scope(state: /State.series, action: /Action.series) {
				SeriesList()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.leagues) var leagues
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { request in
				leagues.list(
					bowledBy: request.filter.bowler,
					withRecurrence: request.filter.recurrence,
					ordering: request.ordering
				)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapFilterButton:
					state.destination = .filters(.init(recurrence: state.filter.recurrence))
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none

				case let .didTapLeague(id):
					return .run { send in
						guard let league = try await leagues.seriesHost(id) else {
							// TODO: report league not found
							return
						}

						await send(.internal(.didLoadSeriesLeague(league)))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableLeague(league):
					state.destination = .editor(.init(value: .edit(league)))
					return .none

				case let .didLoadSeriesLeague(league):
					state.destination = .series(.init(league: league))
					return .run { _ in
						try await clock.sleep(for: .seconds(1))
						recentlyUsed.didRecentlyUseResource(.leagues, league.id)
					}

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(league):
						return .run { send in
							guard let editable = try await leagues.edit(league.id) else {
								// TODO: report league not found
								return
							}

							await send(.internal(.didLoadEditableLeague(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.default(withId: uuid(), forBowler: state.bowler.id))))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: .init(filter: state.filter, ordering: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(recurrence):
						state.filter.recurrence = recurrence
						return state.list.updateQuery(to: .init(filter: state.filter, ordering: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.series(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.series(.internal))),
						.destination(.presented(.series(.view))),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.filters(.binding))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))):
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
}

extension LeaguesList.State {
	var filters: LeaguesFilter.State {
		get { .init(recurrence: filter.recurrence) }
		set { filter.recurrence = newValue.recurrence }
	}
}
