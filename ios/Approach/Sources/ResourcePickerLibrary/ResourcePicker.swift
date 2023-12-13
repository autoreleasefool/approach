import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import ListContentLibrary
import ViewsLibrary

public protocol PickableResource: Equatable, Identifiable {
	static func pickableModelName(forCount: Int) -> String
}

@Reducer
public struct ResourcePicker<Resource: PickableResource, Query: Equatable>: Reducer {
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

		public init(selected: Set<Resource.ID>, query: Query, limit: Int = 0, showsCancelHeaderButton: Bool = true) {
			self.selected = selected
			self.initialSelection = selected
			self.limit = limit
			self.showsCancelHeaderButton = showsCancelHeaderButton
			self.query = query
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case task
			case didFirstAppear
			case didTapCancelButton
			case didTapSaveButton
			case didTapResource(Resource)
			case didTapDeselectAllButton
		}
		@CasePathable public enum DelegateAction {
			case didChangeSelection([Resource])
		}
		@CasePathable public enum InternalAction {
			case refreshObservation
			case didLoadResources(Result<[Resource], Error>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case observation }

	public init(observeResources: @escaping (Query) -> AsyncThrowingStream<[Resource], Error>) {
		self.observeResources = observeResources
	}

	@Dependency(\.dismiss) var dismiss
	let observeResources: (Query) -> AsyncThrowingStream<[Resource], Error>

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .task:
					return .cancelling(id: CancelID.observation)

				case .didFirstAppear:
					return beginObservation(query: state.query)

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
	}
}
