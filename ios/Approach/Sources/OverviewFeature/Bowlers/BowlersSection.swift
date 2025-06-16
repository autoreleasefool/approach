import AnalyticsServiceInterface
import AssetsLibrary
import BowlerDetailsFeature
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import LeaguesListFeature
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

extension Bowler.List: ResourceListSectionItem {}

@Reducer
public struct BowlersSection: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlersFetchRequest) public var fetchRequest
		public var list: ResourceListSection<Bowler.List, Bowler.List.FetchRequest>.State

		@Presents public var destination: Destination.State?
		public var errors: Errors<ErrorID>.State = .init()

		init() {
			let bowlersFetchRequest = Shared(.bowlersFetchRequest)
			self._fetchRequest = bowlersFetchRequest

			self.list = .init(
				features: [.swipeToArchive, .swipeToEdit],
				query: SharedReader(bowlersFetchRequest),
				emptyContent: .init(
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

			case errors(Errors<ErrorID>.Action)
			case list(ResourceListSection<Bowler.List, Bowler.List.FetchRequest>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case details(BowlerDetails)
		case editor(BowlerEditor)
		case leagues(LeaguesList)
		case sortOrder(SortOrderLibrary.SortOrder<Bowler.List.FetchRequest>)
	}

	public enum ErrorID: Hashable, Sendable {
		case bowlerNotFound
		case failedToArchiveBowler
	}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.errors) var errors
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: \.internal.list) {
			ResourceListSection(fetchResources: bowlers.list)
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapBowler(id):
					guard let bowler = state.list.findResource(byId: id) else { return .none }
					state.destination = if featureFlags.isFlagEnabled(.bowlerDetails) {
						.details(BowlerDetails.State(bowler: bowler.summary))
					} else {
						.leagues(LeaguesList.State(bowler: bowler.summary))
					}
					return recentlyUsed.didRecentlyUse(.bowlers, id: id, in: self)

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.$fetchRequest))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableBowler(.success(bowler)):
					state.destination = .editor(BowlerEditor.State(value: .edit(bowler)))
					return .none

				case .didArchiveBowler(.success):
					return .none

				case let .didLoadEditableBowler(.failure(error)):
					return state.errors
						.enqueue(.bowlerNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didArchiveBowler(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveBowler, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
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
						state.destination = .editor(
							BowlerEditor.State(value: .create(.defaultBowler(withId: uuid())))
						)
						return .none

					case .didDelete:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.details(.delegate(.doNothing)))),
						.destination(.presented(.details(.view))),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.leagues(.view))),
						.destination(.presented(.leagues(.internal))),
						.destination(.presented(.leagues(.delegate(.doNothing)))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
						.destination(.presented(.sortOrder(.delegate(.doNothing)))),
						.errors(.view), .errors(.internal), .errors(.delegate(.doNothing)),
						.list(.internal), .list(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadEditableBowler(.failure(error))),
					let .internal(.didArchiveBowler(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: BowlersSection.self)
public struct BowlersSectionView: View {
	public let store: StoreOf<BowlersSection>

	init(store: StoreOf<BowlersSection>) {
		self.store = store
	}

	public var body: some View {
		Section {
			ResourceListSectionView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { bowler in
				LabeledContent(bowler.name, value: format(average: bowler.average))
			}
		} header: {
			header
		}
	}

	@ViewBuilder private var header: some View {
		if !store.list.isEmpty {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Overview.List.bowlers)

				Spacer()

				Menu {
					Button {
						send(.didTapSortOrderButton)
					} label: {
						Label(Strings.Overview.List.Bowlers.sort, systemImage: "arrow.up.arrow.down.square")
					}
				} label: {
					Image(systemName: "ellipsis")
						.frame(width: .smallerIcon, height: .smallerIcon)
						.contentShape(.rect)
				}
				.menuStyle(ButtonMenuStyle())
			}
		}
	}
}

// MARK: - ViewModifier

public struct BowlersSectionViewModifier: ViewModifier {
	@Bindable public var store: StoreOf<BowlersSection>

	public func body(content: Content) -> some View {
		content
			.connectingDataSource(store.scope(state: \.list, action: \.internal.list))
			.details($store.scope(state: \.destination?.details, action: \.internal.destination.details))
			.editor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
			.leagues($store.scope(state: \.destination?.leagues, action: \.internal.destination.leagues))
			.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
	}
}

extension View {
	func connectingBowlersSection(_ store: StoreOf<BowlersSection>) -> some View {
		self.modifier(BowlersSectionViewModifier(store: store))
	}
}

// MARK: - Destinations

extension View {
	fileprivate func details(_ store: Binding<StoreOf<BowlerDetails>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<BowlerDetails>) in
			BowlerDetailsView(store: store)
		}
	}

	fileprivate func editor(_ store: Binding<StoreOf<BowlerEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func leagues(_ store: Binding<StoreOf<LeaguesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<LeaguesList>) in
			LeaguesListView(store: store)
		}
	}

	fileprivate func sortOrder(
		_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Bowler.List.FetchRequest>>?>
	) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Bowler.List.FetchRequest>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}

// MARK: - Preview

#Preview {
	List {
		BowlersSectionView(store: Store(
			initialState: BowlersSection.State(),
			reducer: { BowlersSection() }
		))
	}
}
