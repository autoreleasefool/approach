import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import StringsLibrary
import ViewsLibrary

public protocol ResourceListItem: Equatable, Identifiable {
	var name: String { get }
}

public struct ResourceList<
	R: ResourceListItem,
	Q: Equatable
>: ReducerProtocol {
	public typealias OnDelete = (R) async throws -> Void

	public struct State: Equatable {
		public var features: [Feature]
		public var query: Q
		public var resources: IdentifiedArrayOf<R>?
		public var listTitle: String?

		public var error: Error?
		public let emptyContent: ResourceListEmptyContent

		public var alert: AlertState<AlertAction>?

		public init(
			features: [Feature],
			query: Q,
			listTitle: String?,
			emptyContent: ResourceListEmptyContent
		) {
			self.features = features
			self.query = query
			self.listTitle = listTitle
			self.emptyContent = emptyContent
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

		var emptyState: ResourceListEmpty.State {
			.init(content: emptyContent, style: .empty)
		}

		var errorState: ResourceListEmpty.State {
			switch error {
			case .none:
				return emptyState
			case .failedToDelete:
				return .init(
					content: .init(image: .errorNotFound, title: Strings.Error.Generic.title, action: Strings.Action.reload),
					style: .error
				)
			case .failedToLoad:
				return .init(
					content: .init(
						image: .errorNotFound,
						title: Strings.Error.Generic.title,
						message: Strings.Error.loadingFailed,
						action: Strings.Action.reload
					),
					style: .error
				)
			}
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapAddButton
			case didSwipeToDelete(R)
			case didSwipeToEdit(R)
			case didTap(R)
			case alert(AlertAction)
			case empty(ResourceListEmpty.Action)
		}

		public enum DelegateAction: Equatable {
			case didDelete(R)
			case didEdit(R)
			case didTap(R)
			case didAddNew
			case didTapEmptyStateButton
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
		case tappable
		case add
	}

	struct CancelObservationID {}

	public init(fetchResources: @escaping (Q) -> AsyncThrowingStream<[R], Swift.Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: (Q) -> AsyncThrowingStream<[R], Swift.Error>

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					state.error = nil
					return beginObservation(query: state.query)

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

				case let .didTap(resource):
					guard state.features.contains(.tappable) else {
						fatalError("\(Self.self) did not specify `didTap` feature")
					}

					return .task { .delegate(.didTap(resource)) }

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

				case .empty(.delegate(.didTapButton)):
					switch state.error {
					case .failedToLoad:
						state.error = nil
						return beginObservation(query: state.query)
					case .failedToDelete:
						state.error = nil
						return beginObservation(query: state.query)
					case .none:
						// No error indicates the empty state was shown
						return .task { .delegate(.didTapEmptyStateButton) }
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .observeData:
					state.error = nil
					return beginObservation(query: state.query)

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

	private func beginObservation(query: Q) -> EffectTask<Action> {
		return .run { send in
			for try await resources in fetchResources(query) {
				await send(.internal(.resourcesResponse(.success(resources))))
			}
		} catch: { error, send in
			await send(.internal(.resourcesResponse(.failure(error))))
		}
		.cancellable(id: CancelObservationID.self, cancelInFlight: true)
	}
}

// MARK: - Error

extension ResourceList {
	public enum Error: Equatable {
		case failedToLoad
		case failedToDelete
	}
}

// MARK: - AlertAction

extension ResourceList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(R)
		case dismissed
	}
}

extension ResourceList {
	static func alert(toDelete resource: R) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(resource.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(resource))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}
