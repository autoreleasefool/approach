import AlleyEditorFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

extension Alley.Summary: ResourceListItem {}

public struct AlleysList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Alley.Summary, Alley.Summary.FetchRequest>.State
		public var filter: Alley.Summary.FetchRequest.Filter = .init()

		@PresentationState public var destination: Destination.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.alleys) var alleys
						try await alleys.delete($0.id)
					}),
				],
				query: .init(filter: filter, ordering: .byRecentlyUsed),
				listTitle: Strings.Alley.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.alleys,
					title: Strings.Alley.Error.Empty.title,
					message: Strings.Alley.Error.Empty.message,
					action: Strings.Alley.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapFiltersButton
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableAlley(Alley.EditWithLanes)
			case list(ResourceList<Alley.Summary, Alley.Summary.FetchRequest>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(AlleyEditor.State)
			case filters(AlleysFilter.State)
		}

		public enum Action: Equatable {
			case editor(AlleyEditor.Action)
			case filters(AlleysFilter.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				AlleyEditor()
			}
			Scope(state: /State.filters, action: /Action.filters) {
				AlleysFilter()
			}
		}
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.analytics) var analytics
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { request in
				alleys.filteredList(
					withMaterial: request.filter.material,
					withPinFall: request.filter.pinFall,
					withMechanism: request.filter.mechanism,
					withPinBase: request.filter.pinBase,
					ordered: request.ordering
				)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapFiltersButton:
					state.destination = .filters(.init(filter: state.filter))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableAlley(alley):
					state.destination = .editor(.init(value: .edit(alley)))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(alley):
						return .run { send in
							guard let editable = try await alleys.edit(alley.id) else {
								// TODO: report alley not found
								return
							}

							await send(.internal(.didLoadEditableAlley(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.default(withId: uuid()))))
						return .none

					case .didDelete:
						return .run { _ in await analytics.trackEvent(Analytics.Alley.Deleted()) }

					case .didTap:
						return .none
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(filter):
						state.filter = filter
						return state.list.updateQuery(to: .init(filter: filter, ordering: .byRecentlyUsed))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))):
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
