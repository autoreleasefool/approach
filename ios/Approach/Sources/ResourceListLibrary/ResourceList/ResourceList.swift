import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public protocol ResourceListItem: Equatable, Identifiable {
	var name: String { get }
}

public struct ResourceList<
	R: ResourceListItem,
	Q: Equatable
>: Reducer {
	public struct State: Equatable {
		public var sectionList: SectionResourceList<R, Q>.State

		public var query: Q { sectionList.query }
		public var resources: IdentifiedArrayOf<R>? { sectionList.resources }
		public var editMode: EditMode { sectionList.editMode }

		public func findResource(byId: R.ID) -> R? {
			sectionList.sections?.first?.items[id: byId]
		}

		public init(
			features: [SectionResourceList<R, Q>.Feature],
			query: Q,
			listTitle: String?,
			emptyContent: ResourceListEmptyContent
		) {
			self.sectionList = .init(
				features: features,
				query: query,
				listTitle: listTitle,
				emptyContent: emptyContent
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable { case doNothing }

		public enum DelegateAction: Equatable {
			case didDelete(R)
			case didArchive(R)
			case didEdit(R)
			case didTap(R)
			case didMove(source: IndexSet, destination: Int)
			case didAddNew
			case didTapEmptyStateButton
		}

		public enum InternalAction: Equatable {
			case sectionList(SectionResourceList<R, Q>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init(fetchResources: @escaping (Q) -> AsyncThrowingStream<[R], Swift.Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: (Q) -> AsyncThrowingStream<[R], Swift.Error>

	public var body: some ReducerOf<Self> {
		Scope(state: \.sectionList, action: /Action.internal..Action.InternalAction.sectionList) {
			SectionResourceList(fetchSections: fetchResources(query:))
		}

		Reduce<State, Action> { _, action in
			switch action {
			case .view(.doNothing):
				return .none

			case let .internal(internalAction):
				switch internalAction {
				case let .sectionList(.delegate(delegateAction)):
					switch delegateAction {
					case .didAddNew:
						return .send(.delegate(.didAddNew))

					case let .didArchive(resource):
						return .send(.delegate(.didArchive(resource)))

					case let .didDelete(resource):
						return .send(.delegate(.didDelete(resource)))

					case let .didEdit(resource):
						return .send(.delegate(.didEdit(resource)))

					case let .didTap(resource):
						return .send(.delegate(.didTap(resource)))

					case let .didMove(_, source, destination):
						return .send(.delegate(.didMove(source: source, destination: destination)))

					case .didTapEmptyStateButton:
						return .send(.delegate(.didTapEmptyStateButton))
					}

				case .sectionList(.internal), .sectionList(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func fetchResources(query: Q) -> AsyncThrowingStream<[SectionResourceList<R, Q>.Section], Swift.Error> {
		return .init { continuation in
			let task = Task {
				do {
					for try await resources in self.fetchResources(query) {
						continuation.yield([.init(id: "", items: .init(uniqueElements: resources))])
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}

			continuation.onTermination = { _ in task.cancel() }
		}
	}
}

extension ResourceList.State {
	public mutating func updateQuery(to query: Q) -> Effect<ResourceList.Action> {
		self.sectionList.updateQuery(to: query)
			.map { .internal(.sectionList($0)) }
	}
}
