import ComposableArchitecture
import FeatureActionLibrary
import ViewsLibrary

public protocol PickableResource: Equatable, Identifiable {
	static func pickableModelName(forCount: Int) -> String
}

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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapCancelButton
			case didTapSaveButton
			case didTapResource(Resource)
			case didTapDeselectAllButton
		}
		public enum DelegateAction: Equatable {
			case didChangeSelection([Resource])
		}
		public enum InternalAction: Equatable {
			case observeData
			case didLoadResources(TaskResult<[Resource]>)
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
				case .didObserveData:
					state.error = nil
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
				case .observeData:
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
	}

	private func beginObservation(query: Query) -> Effect<Action> {
		return .run { send in
			for try await resources in observeResources(query) {
				await send(.internal(.didLoadResources(.success(resources))))
			}
		} catch: { error, send in
			await send(.internal(.didLoadResources(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}
}

extension ResourcePicker.State {
	public mutating func updateQuery(to query: Query) -> Effect<ResourcePicker.Action> {
		self.query = query
		return .send(.internal(.observeData))
	}
}
