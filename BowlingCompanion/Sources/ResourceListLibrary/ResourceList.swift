import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import ViewsLibrary

public protocol ResourceListItem: Equatable, Identifiable {
	var name: String { get }
}

public struct ResourceList<R: ResourceListItem, Q: Equatable>: ReducerProtocol {
	public typealias OnDelete = (R) async throws -> Void

	public struct State: Equatable {
		public var features: [Feature]
		public var query: Q
		public var resources: IdentifiedArrayOf<R>?

		public var error: ResourceListError?
		public var alert: AlertState<AlertAction>?

		public init(features: [Feature], query: Q) {
			self.features = features
			self.query = query
		}

		var hasDeleteFeature: Bool { onDelete != nil }
		var onDelete: OnDelete? {
			features.compactMap {
				guard case let .swipeToDelete(onDelete) = $0 else {
					return nil
				}
				return onDelete.wrapped
			}.first
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapErrorButton
			case didTapAddButton
			case didSwipeToDelete(R)
			case didSwipeToEdit(R)
			case alert(AlertAction)
		}

		public enum DelegateAction: Equatable {
			case didDelete(R)
			case didEdit(R)
			case didAddNew
		}

		public enum InternalAction: Equatable {
			case observeData
			case cancelObservation
			case resourcesResponse(TaskResult<[R]>)
			case deleteResponse(TaskResult<R>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum Feature: Equatable {
		case swipeToDelete(onDelete: AlwaysEqual<(R) async throws -> Void>)
		case swipeToEdit
		case add
	}

	struct CancelObservationID {}

	public init(fetchResources: @escaping (Q) -> AsyncThrowingStream<[R], Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: (Q) -> AsyncThrowingStream<[R], Error>

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return .task { .internal(.observeData) }

				case .didTapErrorButton:
					if state.error == .failedToDelete {
						return .task { .internal(.observeData) }
					} else if state.error == .failedToLoad {
							return .task { .internal(.observeData) }
					}

					return .none

				case let .didSwipeToDelete(resource):
					guard state.hasDeleteFeature else {
						fatalError("\(Self.self) did not specify `swipeToDelete` feature")
					}

					state.alert = Self.alert(toDelete: resource)
					return .none

				case let .didSwipeToEdit(resource):
					guard state.features.contains(.swipeToEdit) else {
						fatalError("\(Self.self) did not specify `swipeToEdit` feature")
					}

					return .task { .delegate(.didEdit(resource)) }

				case .didTapAddButton:
					guard state.features.contains(.add) else {
						fatalError("\(Self.self) did not specify `add` feature")
					}

					return .task { .delegate(.didAddNew) }

				case let .alert(.deleteButtonTapped(resource)):
					state.alert = nil
					guard let onDelete = state.onDelete else {
						fatalError("\(Self.self) did not specify `swipeToDelete` feature")
					}
					return .task {
						return await .internal(.deleteResponse(TaskResult {
							try await onDelete(resource)
							return resource
						}))
					}

				case .alert(.dismissed):
					state.alert = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .observeData:
					state.error = nil
					return .run { [query = state.query] send in
						for try await resources in fetchResources(query) {
							await send(.internal(.resourcesResponse(.success(resources))))
						}
					} catch: { error, send in
						await send(.internal(.resourcesResponse(.failure(error))))
					}
					.cancellable(id: CancelObservationID.self, cancelInFlight: true)

				case .cancelObservation:
					return .cancel(id: CancelObservationID.self)

				case let .resourcesResponse(.success(resources)):
					state.resources = .init(uniqueElements: resources)
					return .none

				case .resourcesResponse(.failure):
					state.error = .failedToLoad
					return .none

				case let .deleteResponse(.success(resource)):
					return .task { .delegate(.didDelete(resource))}

				case .deleteResponse(.failure):
					state.error = .failedToDelete
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
