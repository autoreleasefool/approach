import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import TeamEditorFeature
import TeamsRepositoryInterface

extension Team.List: ResourceListSectionItem {}

@Reducer
public struct TeamsSection: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.teamsFetchRequest) public var fetchRequest
		public var list: ResourceListSection<Team.List, Team.List.FetchRequest>.State

		@Presents public var destination: Destination.State?
		public var errors: Errors<ErrorID>.State = .init()

		init() {
			let teamsFetchRequest = Shared(.teamsFetchRequest)
			self._fetchRequest = teamsFetchRequest

			self.list = .init(
				features: [.swipeToArchive, .swipeToEdit],
				query: SharedReader(teamsFetchRequest),
				emptyContent: .init(
					title: Strings.Team.Error.Empty.title,
					message: Strings.Team.Error.Empty.message,
					action: Strings.Team.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapTeam(Team.ID)
			case didTapSortOrderButton
		}

		@CasePathable
		public enum Internal {
			case didArchiveTeam(Result<Team.List, Error>)

			case errors(Errors<ErrorID>.Action)
			case list(ResourceListSection<Team.List, Team.List.FetchRequest>.Action)
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
		case editor(TeamEditor)
		case sortOrder(SortOrderLibrary.SortOrder<Team.List.FetchRequest>)
	}

	public enum ErrorID: Hashable, Sendable {
		case teamNotFound
		case failedToArchive
	}

	@Dependency(TeamsRepository.self) var teams
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: \.internal.list) {
			ResourceListSection(fetchResources: teams.list)
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapTeam(id):
					guard let team = state.list.findResource(byId: id) else { return .none }
					// TODO: Show team details
					return recentlyUsed.didRecentlyUse(.teams, id: team.id, in: self)

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.$fetchRequest))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didArchiveTeam(.success):
					return .none

				case let .didArchiveTeam(.failure(error)):
					return state.errors
						.enqueue(.failedToArchive, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didEdit:
						// TODO: edit team
						return .none

					case let .didArchive(team):
						return .run { send in
							await send(.internal(.didArchiveTeam(Result {
								// TODO: archive team
								team
							})))
						}

					case .didTapEmptyStateButton:
						state.destination = .editor(TeamEditor.State(value: .create(.defaultTeam(withId: uuid()))))
						return .none

					case .didDelete:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
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
			// TODO: Return error for failing to edit team
			case let .internal(.didArchiveTeam(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: TeamsSection.self)
public struct TeamsSectionView: View {
	public let store: StoreOf<TeamsSection>

	init(store: StoreOf<TeamsSection>) {
		self.store = store
	}

	public var body: some View {
		Section {
			ResourceListSectionView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { team in
				VStack(spacing: .unitSpacing) {
					Text(team.name)
					Text(team.bowlers.map(\.name).joined(separator: ", "))
				}
			}
		} header: {
			header
		}
	}

	@ViewBuilder private var header: some View {
		if !store.list.isEmpty {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Overview.List.teams)

				Spacer()

				Menu {
					Button {
						send(.didTapSortOrderButton)
					} label: {
						Label(Strings.Overview.List.Teams.sort, systemImage: "arrow.up.arrow.down.square")
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

public struct TeamsSectionViewModifier: ViewModifier {
	@Bindable public var store: StoreOf<TeamsSection>

	public func body(content: Content) -> some View {
		content
			.connectingDataSource(store.scope(state: \.list, action: \.internal.list))
			.editor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
			.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
	}
}

extension View {
	func connectingTeamsSection(_ store: StoreOf<TeamsSection>) -> some View {
		self.modifier(TeamsSectionViewModifier(store: store))
	}
}

// MARK: - Destinations

extension View {
	fileprivate func editor(_ store: Binding<StoreOf<TeamEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<TeamEditor>) in
			NavigationStack {
				TeamEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(
		_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Team.List.FetchRequest>>?>
	) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Team.List.FetchRequest>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
