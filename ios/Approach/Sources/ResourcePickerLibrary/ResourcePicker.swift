import ComposableArchitecture
import ComposableExtensionsLibrary
import FeatureActionLibrary
import ListContentLibrary
import ViewsLibrary

public protocol PickableResource: Equatable, Identifiable, Sendable {
	static func pickableModelName(forCount: Int) -> String
}

@Reducer
public struct ResourcePicker<Resource: PickableResource, Query: Equatable & Sendable>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var resources: IdentifiedArrayOf<Resource>?
		public var selected: Set<Resource.ID>
		public var initialSelection: Set<Resource.ID>
		public var query: Query
		public var limit: Int
		public var error: ListErrorContent?
		public var showsCancelHeaderButton: Bool

		public var selectedResources: [Resource]? {
			guard let resources else { return nil }
			return selected.compactMap { resources[id: $0] }
		}

		public var initiallySelectedResources: [Resource]? {
			guard let resources else { return nil }
			return initialSelection.compactMap { resources[id: $0] }
		}

		var listState: ListContentState<Resource, ListErrorContent> {
			if let error {
				.error(error)
			} else if let resources {
				.loaded(resources)
			} else {
				.loading
			}
		}

		var isCancellable: Bool { showsCancelHeaderButton && selected != initialSelection }

		public init(selected: Set<Resource.ID>, query: Query, limit: Int = 0, showsCancelHeaderButton: Bool = true) {
			self.selected = selected
			self.initialSelection = selected
			self.limit = limit
			self.showsCancelHeaderButton = showsCancelHeaderButton
			self.query = query
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didTapCancelButton
			case didTapSaveButton
			case didTapResource(Resource)
			case didTapDeselectAllButton
		}
		@CasePathable
		public enum Delegate {
			case didChangeSelection([Resource])
		}
		@CasePathable
		public enum Internal {
			case refreshObservation
			case didLoadResources(Result<[Resource], Error>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	enum CancelID: Sendable { case observation }

	public init(observeResources: @escaping @Sendable (Query) -> AsyncThrowingStream<[Resource], Error>) {
		self.observeResources = observeResources
	}

	@Dependency(\.dismiss) var dismiss
	let observeResources: @Sendable (Query) -> AsyncThrowingStream<[Resource], Error>

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return beginObservation(query: state.query)

				case .task:
					return .cancelling(id: CancelID.observation)

				case .didTapCancelButton:
					state.selected = state.initialSelection
					return .concatenate(
						.send(.delegate(.didChangeSelection(state.initiallySelectedResources ?? []))),
						.run { _ in await dismiss() }
					)

				case .didTapDeselectAllButton:
					state.selected.removeAll()
					return .concatenate(
						.send(.delegate(.didChangeSelection([]))),
						.run { _ in await dismiss() }
					)

				case .didTapSaveButton:
					return .run { _ in await dismiss() }

				case let .didTapResource(resource):
					if state.selected.contains(resource.id) {
						state.selected.remove(resource.id)
					} else if state.limit == 1 {
						state.selected.removeAll()
						state.selected.insert(resource.id)
						return .concatenate(
							.send(.delegate(.didChangeSelection([resource]))),
							.run { _ in await dismiss() }
						)
					} else if state.selected.count < state.limit || state.limit <= 0 {
						state.selected.insert(resource.id)
					}
					return .send(.delegate(.didChangeSelection(state.selectedResources ?? [])))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .refreshObservation:
					state.error = nil
					return beginObservation(query: state.query)

				case let .didLoadResources(.success(resources)):
					state.resources = .init(uniqueElements: resources)
					return .none

				case .didLoadResources(.failure):
					state.error = .loadError
					return .none
				}

			case .delegate:
				return .none
			}
		}

		// TODO: Add Breadcrumb + Analaytics to ResourcePickerLibrary
//		BreadcrumbReducer<State, Action> { _, action in
//			switch action {
//			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
//			default: return nil
//			}
//		}

		// TODO: Add ErrorHandler to ResourcePickerLibrary
//		ErrorHandlerReducer<State, Action> { _, action in
//			switch action {
//			case let .internal(.didLoadResources(.failure(error))):
//				return error
//			default:
//				return nil
//			}
//		}
	}
}
