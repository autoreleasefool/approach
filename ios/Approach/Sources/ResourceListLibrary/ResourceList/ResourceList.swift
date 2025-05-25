import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public protocol ResourceListItem: Equatable, Identifiable, Sendable where ID: Sendable {
	var name: String { get }
}

@Reducer
public struct ResourceList<
	R: ResourceListItem,
	Q: Equatable & Sendable
>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var sectionList: SectionResourceList<R, Q>.State

		public var resources: IdentifiedArrayOf<R>? { sectionList.resources }
		public var editMode: EditMode { sectionList.editMode }

		public func findResource(byId: R.ID) -> R? {
			sectionList.sections?.first?.items[id: byId]
		}

		public init(
			features: [SectionResourceList<R, Q>.Feature],
			query: SharedReader<Q>,
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

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }

		@CasePathable
		public enum Delegate {
			case didDelete(R)
			case didArchive(R)
			case didEdit(R)
			case didTap(R)
			case didMove(source: IndexSet, destination: Int)
			case didAddNew
			case didTapEmptyStateButton
		}

		@CasePathable
		public enum Internal {
			case sectionList(SectionResourceList<R, Q>.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public init(fetchResources: @escaping @Sendable (Q) -> AsyncThrowingStream<[R], Swift.Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: @Sendable (Q) -> AsyncThrowingStream<[R], Swift.Error>

	public var body: some ReducerOf<Self> {
		Scope(state: \.sectionList, action: \.internal.sectionList) {
			SectionResourceList { @Sendable in
				fetchResources(query: $0)
			}
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

				case .sectionList(.internal), .sectionList(.view), .sectionList(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
