import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import GamesListFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SeriesRepositoryInterface
import StringsLibrary
import ViewsLibrary

extension Series.Summary: ResourceListItem {
	public var name: String { date.longFormat }
}

public struct SeriesList: Reducer {
	public struct State: Equatable {
		public let league: League.SeriesHost

		public var list: ResourceList<Series.Summary, League.ID>.State

		@PresentationState public var destination: Destination.State?

		public init(league: League.SeriesHost) {
			self.league = league
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.series) var series: SeriesRepository
						try await series.delete($0.id)
					}),
				],
				query: league.id,
				listTitle: Strings.Series.List.title,
				emptyContent: .init(
					image: .emptySeries,
					title: Strings.Series.Error.Empty.title,
					message: Strings.Series.Error.Empty.message,
					action: Strings.Series.List.add
				))
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapSeries(Series.ID)
		}

		public enum InternalAction: Equatable {
			case didLoadEditableSeries(Series.Edit)
			case list(ResourceList<Series.Summary, League.ID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		public enum DelegateAction: Equatable {}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
			public enum State: Equatable {
				case editor(SeriesEditor.State)
				case games(GamesList.State)
			}

			public enum Action: Equatable {
				case editor(SeriesEditor.Action)
				case games(GamesList.Action)
			}

			public var body: some ReducerOf<Self> {
				Scope(state: /State.editor, action: /Action.editor) {
					SeriesEditor()
				}
				Scope(state: /State.games, action: /Action.games) {
					GamesList()
				}
			}
		}

	public init() {}

	@Dependency(\.date) var date
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList {
				series.list(bowledIn: $0, ordering: .byDate)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapSeries(id):
					if let series = state.list.resources?[id: id] {
						state.destination = .games(.init(series: series))
					}
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableSeries(series):
					state.destination = .editor(.init(value: .edit(series), inLeague: state.league))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(series):
						return .run { send in
							guard let editable = try await self.series.edit(series.id) else {
								// TODO: report series not found
								return
							}

							await send(.internal(.didLoadEditableSeries(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(
							value: .create(.default(withId: uuid(), onDate: date(), inLeague: state.league)),
							inLeague: state.league
						))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.games(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.view), .list(.internal):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.games(.internal))):
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

extension ListErrorContent {
	static let createError = Self(
		title: Strings.Series.Error.FailedToCreate.title,
		message: Strings.Series.Error.FailedToCreate.message,
		action: Strings.Action.tryAgain
	)
}
