import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public protocol ResourceListSectionItem: Equatable, Identifiable, Sendable where ID: Sendable {
	var name: String { get }
}

@Reducer
public struct ResourceListSection<
	R: ResourceListSectionItem,
	Q: Equatable & Sendable
>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@SharedReader public var query: Q
		public var resources: IdentifiedArrayOf<R>?
		public var listTitle: String?

		public var emptyState: ResourceListSectionEmpty.State
		public var errorState: ResourceListSectionEmpty.State?

		public let features: [Feature]

		@Presents public var alert: AlertState<AlertAction>?

		var listContent: ListContent {
			if errorState != nil {
				.error
			} else if let resources {
				.loaded(resources)
			} else {
				.notLoaded
			}
		}

		public var isEmpty: Bool {
			guard let resources else { return true }
			return resources.isEmpty
		}

		public func findResource(byId: R.ID) -> R? {
			resources?[id: byId]
		}

		func hasFeature(_ feature: Feature) -> Bool {
			if !features.contains(feature) {
				assertionFailure("\(Self.self) did not specify \(feature) as a feature.")
				return false
			}

			return true
		}

		public init(
			features: [Feature],
			query: SharedReader<Q>,
			listTitle: String? = nil,
			emptyContent: ResourceListSectionEmptyContent
		) {
			self.features = features
			self._query = query
			self.listTitle = listTitle
			self.emptyState = .init(content: emptyContent, style: .empty)
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didSwipe(SwipeAction, R)
			case alert(PresentationAction<AlertAction>)
		}

		@CasePathable
		public enum Delegate {
			case didDelete(R)
			case didArchive(R)
			case didEdit(R)
			case didTapEmptyStateButton
		}

		@CasePathable
		public enum Internal {
			case observe(query: Q)
			case resourcesResponse(Result<[R], Error>)
			case empty(ResourceListSectionEmpty.Action)
			case error(ResourceListSectionEmpty.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum SwipeAction {
		case edit
		case delete
		case archive
	}

	public enum AlertAction: Equatable {
		case didTapArchiveButton(R)
		case didTapDeleteButton(R)
		case didTapDismissButton
	}

	public enum Feature: Equatable {
		case swipeToArchive
		case swipeToDelete
		case swipeToEdit
	}

	enum ListContent: Equatable {
		case notLoaded
		case loading
		case loaded(IdentifiedArrayOf<R>)
		case error
	}

	enum CancelID: Sendable { case observation }

	public init(fetchResources: @escaping @Sendable (Q) -> AsyncThrowingStream<[R], Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: @Sendable (Q) -> AsyncThrowingStream<[R], Error>

	public var body: some ReducerOf<Self> {
		Scope(state: \.emptyState, action: \.internal.empty) {
			ResourceListSectionEmpty()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .publisher {
						state.$query.publisher
							.map { .internal(.observe(query: $0)) }
					}

				case .task:
					return .cancelling(id: CancelID.observation)

				case let .didSwipe(.delete, resource):
					guard state.hasFeature(.swipeToDelete) else { return .none }
					state.alert = Self.alert(toDelete: resource)
					return .none

				case let .didSwipe(.archive, resource):
					guard state.hasFeature(.swipeToArchive) else { return .none }
					state.alert = Self.alert(toArchive: resource)
					return .none

				case let .didSwipe(.edit, resource):
					guard state.hasFeature(.swipeToEdit) else { return .none }
					return .send(.delegate(.didEdit(resource)))

				case let .alert(.presented(.didTapDeleteButton(resource))):
					state.alert = nil
					return .send(.delegate(.didDelete(resource)))

				case let .alert(.presented(.didTapArchiveButton(resource))):
					state.alert = nil
					return .send(.delegate(.didArchive(resource)))

				case .alert(.presented(.didTapDismissButton)):
					state.alert = nil
					return state.restartObservation()

				case .alert(.dismiss):
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .observe(query):
					state.errorState = nil
					return beginObservation(query: query)

				case let .resourcesResponse(.success(resources)):
					state.resources = .init(uniqueElements: resources)
					return .none

				case .resourcesResponse(.failure):
					state.errorState = .failedToLoad
					return .none

				case .empty(.delegate(.didTapActionButton)):
					return .send(.delegate(.didTapEmptyStateButton))

				case .error(.delegate(.didTapActionButton)):
					if state.errorState == .failedToLoad {
						return state.restartObservation()
					} else if state.errorState == .failedToDelete {
						return state.restartObservation()
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
		}
		.ifLet(\.errorState, action: \.internal.error) {
			ResourceListSectionEmpty()
		}

		// FIXME: Add ErrorHandler to ResourceListSection
	}
}

public struct ResourceListSectionView<
	R: ResourceListSectionItem,
	Q: Equatable & Sendable,
	Row: View
>: View {
	public typealias ListSection = ResourceListSection<R, Q>
	@Bindable public var store: StoreOf<ListSection>

	let row: (R) -> Row

	public init(
		store: StoreOf<ListSection>,
		@ViewBuilder row: @escaping (R) -> Row
	) {
		self.store = store
		self.row = row
	}

	public var body: some View {
		listContent
			.alert($store.scope(state: \.alert, action: \.view.alert))
			.onAppear { store.send(.view(.onAppear)) }
			.task { await store.send(.view(.task)).finish() }
	}

	@ViewBuilder private var listContent: some View {
		switch store.listContent {
		case .notLoaded:
			Color.clear

		case .loading:
			ListProgressView()

		case let .loaded(resources):
			if resources.isEmpty {
				ResourceListSectionEmptyView(
					store: store.scope(state: \.emptyState, action: \.internal.empty)
				)
			} else {
				ForEach(resources) { resource in
					row(resource)
						.swipeActions(allowsFullSwipe: true) {
							if store.features.contains(.swipeToEdit) {
								EditButton { store.send(.view(.didSwipe(.edit, resource))) }
							}

							if store.features.contains(.swipeToDelete) {
								DeleteButton { store.send(.view(.didSwipe(.delete, resource))) }
							}

							if store.features.contains(.swipeToArchive) {
								ArchiveButton { store.send(.view(.didSwipe(.archive, resource))) }
							}
						}
				}
			}

		case .error:
			if let store = store.scope(state: \.errorState, action: \.internal.error) {
				ResourceListSectionEmptyView(store: store)
			}
		}
	}
}
