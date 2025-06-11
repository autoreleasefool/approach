import AssetsLibrary
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

extension Bowler.List: ResourceListSectionItem {}

@Reducer
public struct BowlersSection: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlersFetchRequest) public var bowlersFetchRequest
		public var bowlers: ResourceListSection<Bowler.List, Bowler.List.FetchRequest>.State

		init() {
			let bowlersFetchRequest = Shared(.bowlersFetchRequest)
			self._bowlersFetchRequest = bowlersFetchRequest

			self.bowlers = .init(
				features: [.swipeToArchive, .swipeToEdit],
				query: SharedReader(bowlersFetchRequest),
				emptyContent: .init(
					image: Asset.Media.EmptyState.bowlers,
					title: Strings.Bowler.Error.Empty.title,
					message: Strings.Bowler.Error.Empty.message,
					action: Strings.Bowler.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapBowler(Bowler.ID)
			case didTapSortOrderButton
		}

		@CasePathable
		public enum Internal {
			case didLoadEditableBowler(Result<Bowler.Edit, Error>)
			case didArchiveBowler(Result<Bowler.List, Error>)

			case bowlers(ResourceListSection<Bowler.List, Bowler.List.FetchRequest>.Action)
		}

		@CasePathable
		public enum Delegate {
			case editBowler(Bowler.Edit)
			case createBowler(Bowler.Create)
			case showBowlerDetails(Bowler.Summary)
			case showSortOrder
			case didReceiveError(ErrorID, Error, message: String)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum ErrorID {
		case bowlerNotFound
		case failedToArchiveBowler
	}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.bowlers, action: \.internal.bowlers) {
			ResourceListSection(fetchResources: bowlers.list)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapBowler(id):
					guard let bowler = state.bowlers.findResource(byId: id) else { return .none }
					return .merge(
						.send(.delegate(.showBowlerDetails(bowler.summary))),
						recentlyUsed.didRecentlyUse(.bowlers, id: id, in: self),
					)

				case .didTapSortOrderButton:
					return .send(.delegate(.showSortOrder))
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableBowler(.success(bowler)):
					return .send(.delegate(.editBowler(bowler)))

				case .didArchiveBowler(.success):
					return .none

				case let .didLoadEditableBowler(.failure(error)):
					return .send(.delegate(.didReceiveError(
						.bowlerNotFound,
						error,
						message: Strings.Error.Toast.dataNotFound
					)))

				case let .didArchiveBowler(.failure(error)):
					return .send(.delegate(.didReceiveError(
						.failedToArchiveBowler,
						error,
						message: Strings.Error.Toast.failedToArchive
					)))

				case let .bowlers(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(bowler):
						return .run { send in
							await send(.internal(.didLoadEditableBowler(Result {
								try await bowlers.edit(bowler.id)
							})))
						}

					case let .didArchive(bowler):
						return .run { send in
							await send(.internal(.didArchiveBowler(Result {
								try await bowlers.archive(bowler.id)
								return bowler
							})))
						}

					case .didTapEmptyStateButton:
						return .send(.delegate(.createBowler(.defaultBowler(withId: uuid()))))

					case .didDelete:
						return .none
					}

				case .bowlers(.internal), .bowlers(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: BowlersSection.self)
public struct BowlersSectionView: View {
	@Bindable public var store: StoreOf<BowlersSection>

	init(store: StoreOf<BowlersSection>) {
		self.store = store
	}

	public var body: some View {
		Section {
			ResourceListSectionView(
				store: store.scope(state: \.bowlers, action: \.internal.bowlers)
			) { bowler in
				LabeledContent(bowler.name, value: format(average: bowler.average))
			}
		} header: {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Overview.List.bowlers)
				Spacer()
				Menu {
					Button {
						send(.didTapSortOrderButton)
					} label: {
						Label("Sort bowlers", systemImage: "arrow.up.arrow.down.square")
					}
				} label: {
					Image(systemName: "ellipsis")
						.frame(width: .smallerIcon, height: .smallerIcon)
						.contentShape(.rect)
				}
			}
		}
	}
}

#Preview {
	List {
		BowlersSectionView(store: Store(
			initialState: BowlersSection.State(),
			reducer: { BowlersSection() }
		))
	}
}
