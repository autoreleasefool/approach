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
>: Reducer {
	public typealias OnDelete = (R) async throws -> Void

	public struct State: Equatable {
		public var features: [Feature]
		public var query: Q
		public var resources: IdentifiedArrayOf<R>?
		public var listTitle: String?

		public var emptyState: ResourceListEmpty.State
		public var errorState: ResourceListEmpty.State?

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
			self.emptyState = .init(content: emptyContent, style: .empty)
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
			case didTapAddButton
			case didSwipeToDelete(R)
			case didSwipeToEdit(R)
			case didTap(R)
			case alert(AlertAction)
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
			case empty(ResourceListEmpty.Action)
			case error(ResourceListEmpty.Action)
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

	enum CancelID { case observation }

	public init(fetchResources: @escaping (Q) -> AsyncThrowingStream<[R], Swift.Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: (Q) -> AsyncThrowingStream<[R], Swift.Error>

	public var body: some Reducer<State, Action> {
		Scope(state: \.emptyState, action: /Action.internal..Action.InternalAction.empty) {
			ResourceListEmpty()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					state.errorState = nil
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

					return .send(.delegate(.didEdit(resource)))

				case let .didTap(resource):
					guard state.features.contains(.tappable) else {
						fatalError("\(Self.self) did not specify `didTap` feature")
					}

					return .send(.delegate(.didTap(resource)))

				case .didTapAddButton:
					guard state.features.contains(.add) else {
						fatalError("\(Self.self) did not specify `add` feature")
					}

					return .send(.delegate(.didAddNew))

				case let .alert(.didTapDeleteButton(resource)):
					state.alert = nil
					guard let onDelete = state.onDelete else {
						fatalError("\(Self.self) did not specify `swipeToDelete` feature")
					}
					return .run { send in
						await send(.internal(.deleteResponse(TaskResult {
							try await onDelete(resource)
							return resource
						})))
					}

				case .alert(.didTapDismissButton):
					state.alert = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .observeData:
					state.errorState = nil
					return beginObservation(query: state.query)

				case .cancelObservation:
					return .cancel(id: CancelID.observation)

				case let .resourcesResponse(.success(resources)):
					state.resources = .init(uniqueElements: resources)
					return .none

				case .resourcesResponse(.failure):
					state.errorState = .failedToLoad
					return .none

				case let .deleteResponse(.success(resource)):
					return .send(.delegate(.didDelete(resource)))

				case .deleteResponse(.failure):
					state.errorState = .failedToDelete
					return .none

				case .empty(.delegate(.didTapActionButton)):
					return .send(.delegate(.didTapEmptyStateButton))

				case .error(.delegate(.didTapActionButton)):
					if state.errorState == .failedToLoad {
						state.errorState = nil
						return beginObservation(query: state.query)
					} else if state.errorState == .failedToDelete {
						state.errorState = nil
						return beginObservation(query: state.query)
					}
					return .none

				case .empty(.internal), .empty(.view):
					return .none

				case .error(.internal), .error(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}.ifLet(\.errorState, action: /Action.internal..Action.InternalAction.error) {
			ResourceListEmpty()
		}
	}

	private func beginObservation(query: Q) -> Effect<Action> {
		return .run { send in
			for try await resources in fetchResources(query) {
				await send(.internal(.resourcesResponse(.success(resources))))
			}
		} catch: { error, send in
			await send(.internal(.resourcesResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}
}

extension ResourceList.State {
	public mutating func updateQuery(to query: Q) -> Effect<ResourceList.Action> {
		self.query = query
		return .send(.internal(.observeData))
	}
}

// MARK: - AlertAction

extension ResourceList {
	public enum AlertAction: Equatable {
		case didTapDeleteButton(R)
		case didTapDismissButton
	}
}

extension ResourceList {
	static func alert(toDelete resource: R) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(resource.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteButton(resource))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}
