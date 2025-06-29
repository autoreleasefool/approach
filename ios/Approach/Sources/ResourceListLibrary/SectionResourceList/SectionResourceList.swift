import ComposableArchitecture
import ComposableExtensionsLibrary
import EquatablePackageLibrary
import FeatureActionLibrary
import ListContentLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct SectionResourceList<
	R: ResourceListItem,
	Q: Equatable & Sendable
>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var editMode: EditMode = .inactive

		public var features: [Feature]

		@SharedReader public var query: Q
		public var sections: IdentifiedArrayOf<Section>?
		public var listTitle: String?

		public var emptyState: ResourceListEmpty.State
		public var errorState: ResourceListEmpty.State?

		public var resources: IdentifiedArrayOf<R>? {
			guard let sections else { return nil }
			return .init(uniqueElements: sections.flatMap { $0.items })
		}

		public func findResource(byId: R.ID) -> R? {
			guard let sections else { return nil }
			for section in sections {
				if let resource = section.items[id: byId] {
					return resource
				}
			}

			return nil
		}

		@Presents public var alert: AlertState<AlertAction>?

		var listContent: ListContent {
			if errorState != nil {
				.error
			} else if let sections {
				.loaded(sections)
			} else {
				.notLoaded
			}
		}

		public init(
			features: [Feature],
			query: SharedReader<Q>,
			listTitle: String?,
			emptyContent: ResourceListEmptyContent
		) {
			self.features = features
			self._query = query
			self.listTitle = listTitle
			self.emptyState = .init(content: emptyContent, style: .empty)
		}
	}

	public struct Section: Identifiable, Equatable, Sendable {
		public let id: String
		public var title: String?
		public var items: IdentifiedArrayOf<R>

		public init(
			id: String,
			title: String? = nil,
			items: IdentifiedArrayOf<R>
		) {
			self.id = id
			self.title = title
			self.items = items
		}
	}

	public enum Action: FeatureAction, BindableAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didTapAddButton
			case didTapReorderButton
			case didSwipe(SwipeAction, R)
			case didMove(section: Section.ID, source: IndexSet, destination: Int)
			case didTap(R)
			case alert(PresentationAction<AlertAction>)
		}

		@CasePathable
		public enum Delegate {
			case didDelete(R)
			case didArchive(R)
			case didEdit(R)
			case didTap(R)
			case didMove(section: Section.ID, source: IndexSet, destination: Int)
			case didAddNew
			case didTapEmptyStateButton
		}

		@CasePathable
		public enum Internal {
			case observe(query: Q)
			case sectionsResponse(Result<[Section], Error>)
			case empty(ResourceListEmpty.Action)
			case error(ResourceListEmpty.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public enum Feature: Equatable {
		case swipeToArchive
		case swipeToDelete
		case swipeToEdit
		case moveable
		case tappable
		case add
	}

	public enum SwipeAction {
		case edit
		case delete
		case archive
	}

	enum ListContent: Equatable {
		case notLoaded
		case loading
		case loaded(IdentifiedArrayOf<Section>)
		case error
	}

	enum CancelID: Sendable { case observation }

	public init(fetchSections: @escaping @Sendable (Q) -> AsyncThrowingStream<[Section], Swift.Error>) {
		self.fetchSections = fetchSections
	}

	let fetchSections: @Sendable (Q) -> AsyncThrowingStream<[Section], Swift.Error>

	public var body: some ReducerOf<Self> {
		Scope(state: \.emptyState, action: \.internal.empty) {
			ResourceListEmpty()
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
					guard state.features.contains(.swipeToDelete) else {
						fatalError("\(Self.self) did not specify `swipeToDelete` feature")
					}

					state.alert = Self.alert(toDelete: resource)
					return .none

				case let .didSwipe(.edit, resource):
					guard state.features.contains(.swipeToEdit) else {
						fatalError("\(Self.self) did not specify `swipeToEdit` feature")
					}

					return .send(.delegate(.didEdit(resource)))

				case let .didSwipe(.archive, resource):
					guard state.features.contains(.swipeToArchive) else {
						fatalError("\(Self.self) did not specify `swipeToArchive` feature")
					}

					state.alert = Self.alert(toArchive: resource)
					return .none

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

				case let .didMove(sectionId, source, destination):
					guard state.features.contains(.moveable) else {
						fatalError("\(Self.self) did not specify `moveable` feature")
					}

					guard var section = state.sections?[id: sectionId] else { return .none }
					section.items.move(fromOffsets: source, toOffset: destination)
					state.sections?[id: sectionId] = section
					return .send(.delegate(.didMove(section: sectionId, source: source, destination: destination)))

				case .didTapReorderButton:
					guard state.features.contains(.moveable) else {
						fatalError("\(Self.self) did not specify `moveable` feature")
					}

					if state.editMode == .active {
						state.editMode = .inactive
					} else {
						state.editMode = .active
					}
					return .none

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

				case let .sectionsResponse(.success(sections)):
					state.sections = .init(uniqueElements: sections)
					return .none

				case .sectionsResponse(.failure):
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

			case .delegate, .binding:
				return .none
			}
		}.ifLet(\.errorState, action: \.internal.error) {
			ResourceListEmpty()
		}

		// FIXME: Add ErrorHandler to SectionResourceList
//		ErrorHandlerReducer<State, Action> { _, action in
//			switch action {
//			case let .internal(.sectionsResponse(.failure(error))):
//				return error
//			default:
//				return nil
//			}
//		}
	}
}
